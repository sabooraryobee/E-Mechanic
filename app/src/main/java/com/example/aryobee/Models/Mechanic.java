package com.example.aryobee.Models;

public class Mechanic {

        String profileImageUrl;
        String name;
        String phone;
        String carplate;

        public Mechanic(){}

        public Mechanic(String profileImageUrl, String name, String phone,String carplate) {
            this.profileImageUrl = profileImageUrl;
            this.name = name;
            this.phone = phone;
            this.carplate = carplate;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCarplate(){return carplate;}

        public void setCarplate(String carplate){this.carplate = carplate;}


        @Override
        public String toString() {
            return "Mechanic{" +
                    "profileImageUrl='" + profileImageUrl + '\'' +
                    ", name='" + name + '\'' +
                    ", phone='" + phone + '\'' +
                    ", carplate='" + carplate + '\'' +
                    '}';
        }
    }

