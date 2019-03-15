package org.jetlinks.mqtt.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;

import java.nio.charset.StandardCharsets;


/**
 * @author zhouhao
 * @since
 */
public class MqttClientImplTest {


    public static void main(String[] args) throws Exception {
        EventLoopGroup loop = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);

        for (int i = 0; i < 10000; i++) {
            MqttClient mqttClient = new MqttClientImpl(((topic, payload) -> {
                System.out.println(topic + "=>" + payload.toString(StandardCharsets.UTF_8));
            }));
            mqttClient.setEventLoop(loop);
            mqttClient.getClientConfig().setChannelClass(EpollSocketChannel.class);
            mqttClient.getClientConfig().setClientId("test" + i);
            mqttClient.getClientConfig().setUsername("test");
            mqttClient.getClientConfig().setPassword("test");
            mqttClient.getClientConfig().setProtocolVersion(MqttVersion.MQTT_3_1_1);
            mqttClient.getClientConfig().setReconnect(false);
            mqttClient.setCallback(new MqttClientCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                    cause.printStackTrace();
                }

                @Override
                public void onSuccessfulReconnect() {

                }
            });
            MqttConnectResult result = mqttClient.connect("192.168.0.26", 1883)
                    .await()
                    .get();
            if (result.getReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
                System.out.println("error:" + result.getReturnCode());
                mqttClient.disconnect();
            } else {
                System.out.println("success");
//                mqttClient.publish("test", Unpooled.copiedBuffer("{\"type\":\"read-property\"}", StandardCharsets.UTF_8));
            }
        }

    }

}