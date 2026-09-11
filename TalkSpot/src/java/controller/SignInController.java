package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import socket.ProfileService;

@WebServlet(name = "SignInController", urlPatterns = {"/SignInController"})
public class SignInController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String countryCode = request.getParameter("countryCode");
        String contactNo = request.getParameter("contactNo");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (countryCode == null || countryCode.isEmpty()) {
            responseObject.addProperty("message", "Country code is required");
        } else if (contactNo == null || contactNo.isEmpty()) {
            responseObject.addProperty("message", "Phone number is required");
        } else {
            Session s = HibernateUtil.getSessionFactory().openSession();
            Criteria c1 = s.createCriteria(User.class);
            c1.add(Restrictions.eq("countryCode", countryCode));
            c1.add(Restrictions.eq("contactNo", contactNo));
            User user = (User) c1.uniqueResult();
            s.close();

            if (user == null) {
                responseObject.addProperty("message", "No TalkSpot account found for this number");
            } else {
                responseObject.addProperty("status", true);
                responseObject.addProperty("userId", user.getId());
                responseObject.addProperty("firstName", user.getFirstName());
                responseObject.addProperty("lastName", user.getLastName());
                responseObject.addProperty("profileImage", ProfileService.getProfileUrl(user.getId()));
            }
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
    }
}