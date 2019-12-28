package test.bitcoin.test;

import core.bitcoin.net.DnsSeedDiscovery;
import core.bitcoin.util.UnsafeByteArrayOutputStream;
import core.bitcoin.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author dawn
 */
@Slf4j
@Component
public class PeerTest {

    @Scheduled(initialDelay = 1000, fixedDelay = 50000000)
    public void test() throws IOException, NoSuchAlgorithmException, InterruptedException {
        InetSocketAddress address = DnsSeedDiscovery.getAddress();
        log.info("address -> {}", address);

        Socket socket = new Socket(address.getAddress(), address.getPort());

//        socket.connect(address);

        OutputStream output = socket.getOutputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ByteArrayOutputStream stream = new UnsafeByteArrayOutputStream(32);
        Util.int64ToByteStreamLE(System.currentTimeMillis(), stream);
        byte[] message = stream.toByteArray();

        byte[] header = new byte[4 + 12 + 4 + 4];
        Util.uint32ToByteArrayBE(0xf9beb4d9L, header, 0);

        String name = "ping";
        for (int i = 0; i < name.length() && i < 12; i++) {
            header[4 + i] = (byte) (name.codePointAt(i) & 0xFF);
        }

        Util.uint32ToByteArrayLE(message.length, header, 4 + 12);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message, 0, message.length);
        byte[] hash = digest.digest(digest.digest());

        System.arraycopy(hash, 0, header, 4 + 12 + 4, 4);
        out.write(header);
        out.write(message);

        output.write(out.toByteArray());
        socket.shutdownOutput();

        for (int i = 0; i < 500; i++) {
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
            }
            System.out.println("get message from server: " + sb);
            Thread.sleep(1000);
        }

        socket.close();

    }

}
