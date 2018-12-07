package com.sdxxtop.robotproject.socket;

import android.text.TextUtils;
import android.util.Log;

import com.sdxxtop.robotproject.socket.accept.MessageAcceptBean;
import com.sdxxtop.robotproject.socket.send.LoginBean;
import com.sdxxtop.robotproject.socket.send.SendHeartBean;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketControl {
    private static final String TAG = "WebSocketControl";

    private static final String address = "ws://101.132.192.218:50001";
    private URI uri;

    private WebSocketClientImpl webSocketClient;
    private static WebSocketControl webSocketControl;
    private ConnectListener connectListener;

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public static WebSocketControl getInstance() {
        if (webSocketControl == null) {
            webSocketControl = new WebSocketControl();
        }
        return webSocketControl;
    }

    private WebSocketControl() {
        initSocket();
    }

    private void initSocket() {
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (uri == null) {
            throw new IllegalArgumentException("WebSocketClient uri is null ");
        }
        webSocketClient = new WebSocketClientImpl(uri);
        webSocketClient.connect();

        if (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            Log.e(TAG, "正在连接服务器...");
        }
    }

    public boolean isOpen() {
        if (webSocketClient == null) {
            return false;
        }
        return webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN);
    }

    public synchronized void sendMessage(String value) {
        if (webSocketClient == null || TextUtils.isEmpty(value)) {
            return;
        }
        webSocketClient.send(value);
    }

    public void disConnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
    }

    private class WebSocketClientImpl extends WebSocketClient {

        private WebSocketClientImpl(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handShakeData) {
            Log.e(TAG, "onOpen 连接成功！！！");
            String loginJson = LoginBean.getLoginJson();
            Log.e(TAG, "onOpen loginJson : " + loginJson);
            if (webSocketClient != null) {
                webSocketClient.send(loginJson);
            }
        }

        @Override
        public void onMessage(String message) {
            Log.e(TAG, "onMessage message = " + message + " Thread - " + Thread.currentThread());
            String data = handleMessageGetData(message);
            if (connectListener != null && !TextUtils.isEmpty(data)) {
                connectListener.onMessage(data);
            }
        }

        private String handleMessageGetData(String message) {
            String value = "";
            if (!TextUtils.isEmpty(message)) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.has("data")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data != null) {
                            int type = getSocketType(data);
                            return handleType(message, type);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return value;
        }

        private String handleType(String message, int type) {
            String value = "";
            switch (type) {
                case SocketConstantValue.TYPE_HEART:
                    String heartJson = SendHeartBean.getHeartJson();
                    Log.e(TAG, "SendHeartBean.getHeartJson() : " + heartJson);
//                    send(heartJson);
                    break;
                case SocketConstantValue.TYPE_SEND_ANSWER:
                    value = MessageAcceptBean.messageAcceptParse(message);
                    break;
            }
            return value;
        }

        private int getSocketType(JSONObject data) {
            int type = 0;
            if (data.has("action")) {
                String action = data.optString("action");
                switch (action) {
                    case "sendAnswer":
                        type = SocketConstantValue.TYPE_SEND_ANSWER;
                        break;
                    case "heart":
                        type = SocketConstantValue.TYPE_HEART;
                        break;
                }
            }

            return type;
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e(TAG, "onClose code = " + code + ", reason = " + reason + ", remote = " + remote);
            reconnect();
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "onError ex : " + ex);
            if (isClosed()) {
                reconnect();
            }
        }
    }

    public interface ConnectListener {
        void onMessage(String answer);
    }
}
