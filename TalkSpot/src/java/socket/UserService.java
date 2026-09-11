package socket;

import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Chat;
import entity.FriendList;
import entity.Status;
import entity.User;
import entity.UserStatus;
import entity.CallLog;
import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class UserService {

    public static void updateLogInStatus(int userId) {
        updateStatus(userId, Status.ONLINE);
    }

    public static void updateLogOutStatus(int userId) {
        updateStatus(userId, Status.OFFLINE);
    }

    private static void updateStatus(int userId, Status status) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            Transaction tx = s.beginTransaction();
            User fromUser = (User) s.get(User.class, userId);
            if (fromUser != null) {
                fromUser.setStatus(status);
                fromUser.setUpdatedAt(new Date());
                s.update(fromUser);
                tx.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public static void updateFriendChatStatus(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        try {
            Criteria c1 = s.createCriteria(FriendList.class);
            c1.add(Restrictions.eq("userId.id", userId));
            c1.add(Restrictions.eq("status", Status.ACTIVE));
            List<FriendList> myFriends = c1.list();

            Transaction tr = s.beginTransaction();
            for (FriendList myFriend : myFriends) {
                User me = myFriend.getUserId();
                User friend = myFriend.getFriendId();

                if (me.getStatus().equals(Status.ONLINE)) {
                    Criteria c2 = s.createCriteria(Chat.class);
                    Criterion rest1 = Restrictions.and(
                            Restrictions.eq("from", friend),
                            Restrictions.eq("to", me),
                            Restrictions.eq("status", Status.SENT)
                    );
                    c2.add(rest1);
                    List<Chat> chats = c2.list();
                    for (Chat chat : chats) {
                        chat.setStatus(Status.DELIVERED);
                        chat.setUpdatedAt(new Date());
                        s.update(chat);
                    }
                }
            }
            tr.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
    }

    public static Map<String, Object> getFriendData(int friendId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        User friend = (User) s.get(User.class, friendId);
        s.close();
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "friend_data");
        envelope.put("payload", friend);
        return envelope;
    }

    public static Map<String, Object> getAllUsers(int userId) {
        try {
            Session s = HibernateUtil.getSessionFactory().openSession();
            Map<String, Object> map = new HashMap<>();
            List<UserDTO> userDTOs = new ArrayList<>();

            Criteria c1 = s.createCriteria(FriendList.class);
            c1.add(Restrictions.eq("userId.id", userId));
            c1.add(Restrictions.eq("status", Status.ACTIVE));
            List<FriendList> myFriends = c1.list();

            for (FriendList myFriend : myFriends) {
                User user = myFriend.getFriendId();
                UserDTO dto = new UserDTO();
                dto.setId(user.getId());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                dto.setDisplayName(myFriend.getDisplayName());
                dto.setCountryCode(user.getCountryCode());
                dto.setContactNo(user.getContactNo());
                dto.setProfileImage(ProfileService.getProfileUrl(user.getId()));
                dto.setCreatedAt(user.getCreatedAt());
                dto.setUpdatedAt(user.getUpdatedAt());
                dto.setStatus(user.getStatus());
                userDTOs.add(dto);
            }
            s.close();
            map.put("type", "all_users");
            map.put("payload", userDTOs);
            return map;
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> saveNewContact(int myId, User user) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("responseStatus", Boolean.FALSE);

        Criteria c1 = s.createCriteria(User.class);
        c1.add(Restrictions.and(
                Restrictions.eq("countryCode", user.getCountryCode()),
                Restrictions.eq("contactNo", user.getContactNo())
        ));
        User u1 = (User) c1.uniqueResult();
        if (u1 == null) {
            responseObject.addProperty("message", "This user not in TalkSpot");
        } else {
            User me = (User) s.get(User.class, myId);
            Criteria c2 = s.createCriteria(FriendList.class);
            c2.add(Restrictions.and(
                    Restrictions.eq("userId", me),
                    Restrictions.eq("friendId", u1)
            ));
            FriendList friendList = (FriendList) c2.uniqueResult();
            responseObject.addProperty("responseStatus", Boolean.TRUE);
            Transaction tx = s.beginTransaction();
            if (friendList == null) {
                FriendList fl = new FriendList(me, u1, user.getFirstName() + " " + user.getLastName());
                s.save(fl);
                responseObject.addProperty("message", "This user added to friend list");
            } else {
                friendList.setDisplayName(user.getFirstName() + " " + user.getLastName());
                s.update(friendList);
                responseObject.addProperty("message", "This user already in friend list");
            }
            tx.commit();
        }
        s.close();
        Map<String, Object> map = new HashMap<>();
        map.put("type", "new_contact_response_text");
        map.put("payload", responseObject);
        return map;
    }

    public static Map<String, Object> getMyProfileData(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        User user = (User) s.get(User.class, userId);

        Map<String, Object> payload = new HashMap<>();
        if (user != null) {
            payload.put("id", user.getId());
            payload.put("firstName", user.getFirstName());
            payload.put("lastName", user.getLastName());
            payload.put("countryCode", user.getCountryCode());
            payload.put("contactNo", user.getContactNo());
            payload.put("about", user.getAbout() != null ? user.getAbout() : "");

            // Image URL එක Cache Busting පාරක් දමා fast load වෙන ලෙස සකස් කර ඇත
            String imgUrl = ProfileService.getProfileUrl(userId);
            if (imgUrl != null && !imgUrl.isEmpty()) {
                payload.put("profileImage", imgUrl + "?t=" + System.currentTimeMillis());
            } else {
                payload.put("profileImage", null);
            }

            List<String> linkList = new ArrayList<>();
            if (user.getLinks() != null && !user.getLinks().trim().isEmpty()) {
                String[] splitLinks = user.getLinks().split("\n");
                for (String l : splitLinks) {
                    if (!l.trim().isEmpty()) {
                        linkList.add(l.trim());
                    }
                }
            }
            payload.put("links", linkList);
        }
        s.close();

        Map<String, Object> map = new HashMap<>();
        map.put("type", "user_profile");
        map.put("payload", payload);
        return map;
    }

    public static Map<String, Object> updateProfileInfo(int userId, String about, List<String> links) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            User user = (User) s.get(User.class, userId);
            if (user != null) {
                user.setAbout(about == null ? "" : about);

                if (links != null && !links.isEmpty()) {
                    user.setLinks(String.join("\n", links));
                } else {
                    user.setLinks("");
                }

                user.setUpdatedAt(new Date());
                s.update(user);
                tx.commit();
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            s.close();
        }
        return getMyProfileData(userId);
    }

    public static Map<String, Object> uploadStatus(int userId, String mediaUrl) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();

        User user = (User) s.get(User.class, userId);
        UserStatus newStatus = new UserStatus(user, mediaUrl);
        s.save(newStatus);

        tx.commit();

        Criteria cFriends = s.createCriteria(FriendList.class);
        cFriends.add(Restrictions.eq("userId.id", userId));
        cFriends.add(Restrictions.eq("status", Status.ACTIVE));
        List<FriendList> myFriends = cFriends.list();
        s.close();

        for (FriendList f : myFriends) {
            int friendId = f.getFriendId().getId();
            Map<String, Object> friendStatusData = getStatuses(friendId);
            ChatService.sendToUser(friendId, friendStatusData);
        }

        return getStatuses(userId);
    }

    public static Map<String, Object> getStatuses(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();

        Criteria cMy = s.createCriteria(UserStatus.class);
        cMy.add(Restrictions.eq("user.id", userId));
        cMy.addOrder(org.hibernate.criterion.Order.desc("createdAt"));
        List<UserStatus> myStatusList = cMy.list();

        // Friends Statuses Fetching
        Criteria cFriends = s.createCriteria(FriendList.class);
        cFriends.add(Restrictions.eq("userId.id", userId));
        cFriends.add(Restrictions.eq("status", Status.ACTIVE));
        List<FriendList> friendList = cFriends.list();

        List<Map<String, Object>> friendStatuses = new ArrayList<>();
        for (FriendList f : friendList) {
            Criteria cFStatus = s.createCriteria(UserStatus.class);
            cFStatus.add(Restrictions.eq("user.id", f.getFriendId().getId()));
            cFStatus.addOrder(org.hibernate.criterion.Order.desc("createdAt"));
            List<UserStatus> fStatuses = cFStatus.list();

            if (!fStatuses.isEmpty()) {
                UserStatus latest = fStatuses.get(0);
                Map<String, Object> item = new HashMap<>();
                item.put("id", String.valueOf(latest.getId()));
                item.put("name", f.getDisplayName());
                item.put("image", latest.getMediaUrl());
                item.put("time", latest.getCreatedAt().toString());
                friendStatuses.add(item);
            }
        }

        s.close();

        Map<String, Object> myStatusData = new HashMap<>();
        if (!myStatusList.isEmpty()) {
            myStatusData.put("image", myStatusList.get(0).getMediaUrl());
            myStatusData.put("time", myStatusList.get(0).getCreatedAt().toString());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("type", "statuses_data");
        Map<String, Object> payload = new HashMap<>();
        payload.put("myStatus", myStatusData.isEmpty() ? null : myStatusData);
        payload.put("friendStatuses", friendStatuses);
        response.put("payload", payload);

        return response;
    }

    public static Map<String, Object> saveCallLog(int callerId, int receiverId, String callType, String status, String channelName) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();

        User caller = (User) s.get(User.class, callerId);
        User receiver = (User) s.get(User.class, receiverId);

        CallLog log = new CallLog(caller, receiver, callType, status, channelName);
        s.save(log);

        tx.commit();
        s.close();

        return getCallLogs(callerId);
    }

   public static Map<String, Object> getCallLogs(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Criteria c = s.createCriteria(CallLog.class);
        c.add(org.hibernate.criterion.Restrictions.or(
                org.hibernate.criterion.Restrictions.eq("caller.id", userId),
                org.hibernate.criterion.Restrictions.eq("receiver.id", userId)
        ));
        c.addOrder(org.hibernate.criterion.Order.desc("createdAt"));
        List<CallLog> list = c.list();

        List<Map<String, Object>> logs = new ArrayList<>();
        for (CallLog l : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", l.getId());
            item.put("callerId", l.getCaller().getId());     
            item.put("receiverId", l.getReceiver().getId()); 
            
            boolean isOutgoing = (l.getCaller().getId() == userId);
            User otherUser = isOutgoing ? l.getReceiver() : l.getCaller();

            item.put("name", otherUser.getFirstName() + " " + otherUser.getLastName());
            item.put("type", l.getCallType());
            item.put("status", l.getStatus());
            item.put("isOutgoing", isOutgoing);
            item.put("time", l.getCreatedAt().toString());
            item.put("profileImage", ProfileService.getProfileUrl(otherUser.getId()));
            logs.add(item);
        }
        s.close();

        Map<String, Object> response = new HashMap<>();
        response.put("type", "call_logs");
        response.put("payload", logs);
        return response;
    }
    
    public static Map<String, Object> clearAllCallLogs(int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();
        try {
            org.hibernate.Query query = s.createQuery(
                "delete from CallLog c where c.caller.id = :uid or c.receiver.id = :uid"
            );
            query.setParameter("uid", userId);
            query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            s.close();
        }
        return getCallLogs(userId); 
    }
    
    public static boolean deleteForEveryone(int messageId, int userId) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();
        try {
            Chat chat = (Chat) s.get(Chat.class, messageId);
            if (chat != null && chat.getFrom().getId() == userId) {
                s.delete(chat);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            s.close();
        }
        return false;
    }


    

}
