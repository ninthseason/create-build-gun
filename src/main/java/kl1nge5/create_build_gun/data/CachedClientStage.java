package kl1nge5.create_build_gun.data;

public class CachedClientStage {
    // 由于客户端无法直接获取世界阶段，因此客户段需要通过网络连接向服务端请求数据
    // 而收到服务端的响应后，回调函数将异步地把数据存于此处
    public static int stage = 0;
}
