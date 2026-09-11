package socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import entity.Chat;
import entity.Status;
import entity.User;
import hibernate.HibernateUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat")
public class ChatEndPoint {

    private static final Gson GSON = new Gson();
    private int userId;

    @OnOpen
    public void onOpen(Session session) {
        String query = session.getQueryString();
        if (query != null && query.startsWith("userId=")) {
            userId = Integer.parseInt(query.substring("userId=".length()));
            ChatService.register(userId, session);
            UserService.updateLogInStatus(userId);
            UserService.updateFriendChatStatus(userId);
//            ChatService.sendToUser(userId,
//                    ChatService.friendListEnvelope(ChatService.getFriendChatsForUser(userId)));
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (userId > 0) { // userId != null
            ChatService.unregister(userId);
            UserService.updateLogOutStatus(userId);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (userId > 0) {
            UserService.updateLogOutStatus(userId);
        }
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            Map<String, Object> map = ChatEndPoint.GSON.fromJson(message, Map.class);
            String type = (String) map.get("type");
            switch (type) {
                case "PING": {
                    JsonObject responseObject = new JsonObject();
                    responseObject.addProperty("type", "PONG");
                    ChatService.sendToUser(userId, responseObject);
                    break;
                }
                case "send_chat": {
                    int fromId = (int) map.get("fromId");
                    int toId = (int) map.get("toId");
                    String chatText = (String) map.get("message");
                    org.hibernate.Session s = HibernateUtil.getSessionFactory().openSession();
                    User fromUser = (User) s.get(User.class, fromId);
                    User toUser = (User) s.get(User.class, toId);

                    if (fromUser != null && toUser != null) {
                        Chat chat = new Chat(fromUser, chatText, toUser, "", Status.SENT);
                        chat.setCreatedAt(new Date());
                        chat.setUpdatedAt(new Date());
                        ChatService.deliverChat(chat);
                    }
                    break;
                }
                case "get_chat_list": {
                    ChatService.sendToUser(userId,
                            ChatService.friendListEnvelope(ChatService.getFriendChatsForUser(userId)));
                    break;
                }
                case "get_single_chat": {
                    int friendId = (int) ((double) map.get("friendId"));
                    List<Chat> chats = ChatService.getChatHistory(userId, friendId);
                    Map<String, Object> envelop = ChatService.singleChatEnvelope(chats);
                    ChatService.sendToUser(userId, envelop);
                    ChatService.sendToUser(userId,
                            ChatService.friendListEnvelope(ChatService.getFriendChatsForUser(userId)));
                    break;
                }
                case "send_message": {
                    int friendId = (int) ((double) map.get("toUserId"));
                    String chat = String.valueOf(map.get("message"));
                    ChatService.saveNewChat(userId, friendId, chat);
                    break;
                }
                case "get_friend_data": {
                    int friendId = (int) ((double) map.get("friendId"));
                    Map<String, Object> envelope = UserService.getFriendData(friendId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "get_all_users": {
                    Map<String, Object> envelope = UserService.getAllUsers(userId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "save_new_contact": {
                    LinkedTreeMap userObject = (LinkedTreeMap) map.get("user"); //com.google.gson.internal
                    User user = new User(
                            String.valueOf(userObject.get("firstName")),
                            String.valueOf(userObject.get("lastName")),
                            String.valueOf(userObject.get("countryCode")),
                            String.valueOf(userObject.get("contactNo")));
                    Map<String, Object> envelope = UserService.saveNewContact(userId, user);
                    ChatService.sendToUser(userId, envelope);
                    Map<String, Object> e = UserService.getAllUsers(userId);
                    ChatService.sendToUser(userId, e);
                    break;
                }
                case "set_user_profile": {
                    Map<String, Object> envelope = UserService.getMyProfileData(userId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "update_profile_info": {
                    Object aboutObj = map.get("about");
                    String about = aboutObj == null ? "" : String.valueOf(aboutObj);
                    List<String> links = (List<String>) map.get("links");
                    Map<String, Object> envelope = UserService.updateProfileInfo(userId, about, links);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "get_statuses": {
                    Map<String, Object> envelope = UserService.getStatuses(userId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "upload_status": {
                    String mediaUrl = String.valueOf(map.get("mediaUrl"));
                    Map<String, Object> envelope = UserService.uploadStatus(userId, mediaUrl);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "initiate_call": {
                    int toUserId = (int) ((double) map.get("toUserId"));
                    String callType = String.valueOf(map.get("callType"));
                    String callerName = String.valueOf(map.get("callerName"));
                    String channelName = "talkspot_channel_" + System.currentTimeMillis();

                    Map<String, Object> callSignal = new HashMap<>();
                    callSignal.put("type", "incoming_call_signal");
                    callSignal.put("fromUserId", userId);
                    callSignal.put("callerName", callerName);
                    callSignal.put("callType", callType);
                    callSignal.put("channelName", channelName);

                    ChatService.sendToUser(toUserId, callSignal);

                    Map<String, Object> outgoingSignal = new HashMap<>();
                    outgoingSignal.put("type", "outgoing_call_signal");
                    outgoingSignal.put("toUserId", toUserId);
                    outgoingSignal.put("channelName", channelName);
                    ChatService.sendToUser(userId, outgoingSignal);

                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            // Check & Save Missed Call
                            UserService.saveCallLog(userId, toUserId, callType, "MISSED", channelName);

                            Map<String, Object> missedSignal = new HashMap<>();
                            missedSignal.put("type", "call_response_signal");
                            missedSignal.put("action", "MISSED");
                            missedSignal.put("channelName", channelName);

                            ChatService.sendToUser(userId, missedSignal);
                            ChatService.sendToUser(toUserId, missedSignal);

                            ChatService.sendToUser(userId, UserService.getCallLogs(userId));
                            ChatService.sendToUser(toUserId, UserService.getCallLogs(toUserId));
                        }
                    }, 30000);
                    break;
                }

                case "call_response": {
                    int toUserId = (int) ((double) map.get("toUserId"));
                    String action = String.valueOf(map.get("action")); // "ACCEPTED", "REJECTED", "MISSED"
                    String channelName = String.valueOf(map.get("channelName"));
                    String callType = String.valueOf(map.get("callType"));

                    UserService.saveCallLog(toUserId, userId, callType, action, channelName);

                    Map<String, Object> responseSignal = new HashMap<>();
                    responseSignal.put("type", "call_response_signal");
                    responseSignal.put("action", action);
                    responseSignal.put("channelName", channelName);

                    ChatService.sendToUser(toUserId, responseSignal);
                    ChatService.sendToUser(userId, responseSignal);

                    ChatService.sendToUser(userId, UserService.getCallLogs(userId));
                    ChatService.sendToUser(toUserId, UserService.getCallLogs(toUserId));
                    break;
                }
                case "get_call_logs": {
                    ChatService.sendToUser(userId, UserService.getCallLogs(userId));
                    break;
                }
                case "clear_all_call_logs": {
                    Map<String, Object> envelope = UserService.clearAllCallLogs(userId);
                    ChatService.sendToUser(userId, envelope);
                    break;
                }
                case "delete_message_everyone": {
                    int messageId = (int) ((double) map.get("messageId"));
                    int friendId = (int) ((double) map.get("friendId"));

                    boolean isDeleted = UserService.deleteForEveryone(messageId, userId);
                    if (isDeleted) {
                        Map<String, Object> signal = new HashMap<>();
                        signal.put("type", "message_deleted_signal");
                        signal.put("messageId", messageId);

                        ChatService.sendToUser(userId, signal);
                        ChatService.sendToUser(friendId, signal);
                    }
                    break;
                }

               
                
                default: {
                    System.out.println("Ignored unknown client type: " + type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
