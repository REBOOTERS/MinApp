import socket
import sys

def udp_receiver():
    # 创建 UDP socket
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        print("Socket created successfully")
    except socket.error as err:
        print(f"Socket creation failed with error: {err}")
        sys.exit()

    # 设置地址重用选项和广播选项
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)

    # 修改绑定地址：从 255.255.255.255 改为 0.0.0.0
    # 0.0.0.0 表示绑定到所有网络接口，可以接收广播消息
    host = '0.0.0.0'  # 改为绑定到所有接口
    port = 12306

    try:
        sock.bind((host, port))
        print(f"Socket bound to {host}:{port}")
        print("Listening for broadcast messages on all interfaces...")
    except socket.error as err:
        print(f"Socket bind failed with error: {err}")
        sock.close()
        sys.exit()

    print("UDP receiver is listening for broadcast messages...")
    print("Press Ctrl+C to stop the receiver")

    try:
        while True:
            # 接收数据和发送方地址
            data, addr = sock.recvfrom(1024)  # 缓冲区大小为1024字节
            print(f"Received message from {addr[0]}:{addr[1]}: {data.decode('utf-8')}")
    except KeyboardInterrupt:
        print("\nReceiver stopped by user")
    except socket.error as err:
        print(f"Receive failed with error: {err}")
    except UnicodeDecodeError:
        print(f"Received binary data from {addr[0]}:{addr[1]}: {data.hex()}")
    finally:
        sock.close()
        print("Socket closed")

if __name__ == "__main__":
    udp_receiver()