package com.unirest.data.models;

import android.content.Context;

import com.unirest.R;

import java.util.List;

public class NotificationRequest {
        private Notification notificationTemplate;
        private List<User> users;

        public Notification getNotificationTemplate() {
            return notificationTemplate;
        }

        public void setNotificationTemplate(Notification notificationTemplate) {
            this.notificationTemplate = notificationTemplate;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public static NotificationRequest fastCallRequest(List<User> users, Context context) {
            Notification template = new Notification();
            template.setTitle(context.getString(R.string.new_alert));
            template.setContent(context.getString(R.string.you_calling_commandant));
            template.setDate(System.currentTimeMillis());
            NotificationRequest request = new NotificationRequest();
            request.setUsers(users);
            request.setNotificationTemplate(template);
            return request;
        }
    }