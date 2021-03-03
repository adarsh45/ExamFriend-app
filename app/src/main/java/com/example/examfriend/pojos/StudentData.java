package com.example.examfriend.pojos;

public class StudentData {

        private String uid, name, msTeamsId, phoneNumber;
        private int paymentGiven = 0;
        private boolean isAlreadyLoggedIn = false;

        public StudentData(){}

        @Override
        public String toString() {
            return "StudentData{" +
                    "uid='" + uid + '\'' +
                    ", name='" + name + '\'' +
                    ", msTeamsId='" + msTeamsId + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", paymentGiven=" + paymentGiven +
                    ", isAlreadyLoggedIn=" + isAlreadyLoggedIn +
                    '}';
        }

        public StudentData(String uid, String name, String msTeamsId, String phoneNumber, int paymentGiven, boolean isAlreadyLoggedIn) {
            this.uid = uid;
            this.name = name;
            this.msTeamsId = msTeamsId;
            this.phoneNumber = phoneNumber;
            this.paymentGiven = paymentGiven;
            this.isAlreadyLoggedIn = isAlreadyLoggedIn;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMsTeamsId() {
            return msTeamsId;
        }

        public void setMsTeamsId(String msTeamsId) {
            this.msTeamsId = msTeamsId;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public int getPaymentGiven() {
            return paymentGiven;
        }

        public void setPaymentGiven(int paymentGiven) {
            this.paymentGiven = paymentGiven;
        }

        public boolean isAlreadyLoggedIn() {
            return isAlreadyLoggedIn;
        }

        public void setAlreadyLoggedIn(boolean alreadyLoggedIn) {
            isAlreadyLoggedIn = alreadyLoggedIn;
        }

}
