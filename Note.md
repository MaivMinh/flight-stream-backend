1. **PERFORMANCE TUNING**
- Hiện tại, hệ thống đang sử dụng Kafka với vai trò là message broker để triển khai realtime processing. 
- Chúng ta sẽ có 3 phía cần phải tối ưu hiệu suất: 
  1. **Producer**: Tối ưu cách gửi dữ liệu vào Kafka, có thể sử dụng batch processing để giảm overhead.
  2. **Kafka Cluster**: Cấu hình Kafka cluster để đảm bảo hiệu suất cao, bao gồm việc phân vùng (partitioning) phù hợp với traffic.
  3. **Consumer**: Tối ưu cách tiêu thụ dữ liệu từ Kafka, có thể sử dụng multi-threading hoặc parallel processing để tăng tốc độ xử lý. (Sẽ đi vào chi tiết ở phần dưới)

a. **Producer Optimization**:
- Sử dụng batch processing để gửi nhiều message cùng một lúc, giảm overhead của việc gửi từng message riêng lẻ.
- Cấu hình `linger.ms` để cho phép producer chờ một khoảng thời gian ngắn trước khi gửi batch, giúp tăng hiệu suất.
- Sử dụng compression (như gzip hoặc snappy) để giảm kích thước dữ liệu gửi qua mạng.
b. **Kafka Cluster Optimization**:
- Cấu hình số lượng partition phù hợp với lượng traffic và số lượng consumer để đảm bảo cân bằng tải.
- Sử dụng replication để đảm bảo độ tin cậy và khả năng chịu lỗi của hệ thống.

c. **Consumer Optimization**:
- Hiện tại với hệ thống chỉ có 1 instance duy nhất. Chúng ta sẽ ưu tiên việc sử dụng multi-threading cho instance này. Cụ thể thì chúng ta sẽ thiết lập nhiều concurrent consumer threads để fetch các message từ Kafka đồng thời. Việc này giúp tránh bị blocking khi một thread phải xử lý message mất nhiều thời gian, từ đó tăng throughput của hệ thống.
- Vì producer sẽ gửi data theo batch, nên chúng ta cũng sẽ consume theo batch. 
- Thiết lập `max.poll.records` để giới hạn số lượng message được fetch trong mỗi poll, giúp kiểm soát lượng dữ liệu được xử lý đồng thời và tránh quá tải.
- Hiện tại, vì logic consume message của chúng ta khá đơn giản. Nên bottleneck sẽ không nằm ở việc fetch và process message của các consumer thread. Mà bottlneck sẽ nằm ở việc publish message xuống Redis từ Redis Publisher. 
- Cụ thể: Về bản chất thì các Consumer Thread sau khi fetch messsage xong từ Kafka thì chúng sẽ phải kiêm luôn công việc publish message xuống Redis. Và việc publish message xuống Redis có thể là một bottleneck rất lớn. Vì hàm `convertAndSend` là một hàm synchronous. Nên khiến cho Consumer Thread sẽ bị block cho đến khi việc publish message xuống Redis hoàn tất. Điều này sẽ làm giảm hiệu suất của hệ thống, đặc biệt là khi có nhiều message cần được publish đồng thời.
- Để giải quyết vấn đề này, chúng ta sử dụng Executor Service để sinh ra các Virtual Thread cho từng lần submit work.
- Việc này giúp cho việc publish message xuống Redis được bất đồng bộ -> Giải phóng cho Consumer Thread không bị block -> Tăng hiệu suất của hệ thống.
- Ngoài ra, ở Redis Subscriber, giải pháp ban đầu là sử dụng Executor Service giống trên để xử lý các message này. Tuy nhiên, mặc định thì Redis Subscriber sẽ assign một Virtual Thread cho từng message được lấy từ Redis (xem trong hàm onMessage).


**Tổng kết:** Với hệ thống có trung gian là Kafka, thì chúng ta nên tối ưu hiệu suất ở cả 3 phía: Producer, Kafka Cluster và Consumer. Đặc biệt, việc sử dụng multi-threading cho Consumer đúng đắn sẽ giúp tăng hiệu suất cho hệ thống. Tuy nhiên cũng sẽ có những đánh đổi khác nhau, ví dụ như việc sử dụng multi-threading sẽ làm tăng độ phức tạp của hệ thống và có thể dẫn đến các vấn đề về đồng bộ hóa nếu không được quản lý tốt. Do đó, cần phải cân nhắc kỹ lưỡng khi triển khai các giải pháp tối ưu hiệu suất này.