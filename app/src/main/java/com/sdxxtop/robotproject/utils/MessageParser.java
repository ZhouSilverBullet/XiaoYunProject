/*
 *
 *   Copyright (C) 2017 OrionStar Technology Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */
package com.sdxxtop.robotproject.utils;

import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.listener.Person;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageParser {
    private static final String TAG = "MessageParser";
    public static final String RESULT_OK = "ok";
    public static final String RESULT_FAILED = "failed";
    private static final float SWITCH_TRACKING_DISTANCE = 0.2f;
    private static final int NEAR_TRACKING = 15;

    public static Person getOnePerson(Person trackingPerson, List<Person>
            personList, double maxDistance) {
        if (null == personList || personList.size() <= 0) {
            return null;
        }

        Person person = null;
        Person tracking = null;
        Person nearTracking = null;
        for (int i = 0; i < personList.size(); i++) {
            Person newPerson = personList.get(i);
            int id = newPerson.getId();
            double distance = newPerson.getDistance();
            int angle = newPerson.getAngle();
            Log.e(TAG, "getAllPerson distance: " + distance
                    + ", id: " + id + ", angle: " + angle);
        }
        for (int i = 0; i < personList.size(); i++) {
            Person newPerson = personList.get(i);
            int id = newPerson.getId();
            double distance = newPerson.getDistance();
            int angle = newPerson.getAngle();
            if (maxDistance > 0 && distance > maxDistance) {
                continue;
            }

            if (trackingPerson != null
                    && trackingPerson.getId() == id) {
                tracking = newPerson;
            } else {
                if (trackingPerson != null) {
                    float trackingAngle = trackingPerson.getAngle();
                    float nearAngle = (nearTracking == null ?
                            0 : nearTracking.getAngle());
                    float diffAngle = Math.abs(angle - trackingAngle);
                    float nearDiffAngle = Math.abs(nearAngle - trackingAngle);

                    if (diffAngle < NEAR_TRACKING &&
                            (nearTracking == null || diffAngle < nearDiffAngle)) {
                        if (nearTracking == null) {
                            nearTracking = newPerson;
                        }
                    }
                }

                if (person != null) {
                    double currentDistance = person.getDistance();
                    if (currentDistance != 0
                            && (currentDistance < distance || distance == 0)) {
                        continue;
                    } else if (distance == 0) {
                        float currentAngle = person.getAngle();
                        if (Math.abs(currentAngle) < Math.abs(angle)) {
                            continue;
                        }
                    }
                }
                person = newPerson;
            }
        }

        if (person == null) {
            return tracking;
        }

        if (tracking == null) {
            if (nearTracking != null) {
                Log.e(TAG, "switch person detail: tracking angle: "
                        + trackingPerson.getAngle() +
                        ", near angle: " + nearTracking.getAngle());
            } else {
                Log.e(TAG, "switch person detail: person angle: "
                        + person.getAngle() +
                        ", distance: " + person.getDistance());
            }
            return nearTracking != null ? nearTracking : person;
        }

        double trackingDistance = tracking.getDistance();
        double minDistance = person.getDistance();
        if (trackingDistance == 0) {
            return tracking;
        } else {
            if (minDistance == 0) {
                return tracking;
            } else if (trackingDistance - minDistance > SWITCH_TRACKING_DISTANCE) {
                Log.e(TAG, "switch person " +
                        "tracking distance: " + trackingDistance +
                        ", min distance: " + minDistance);
                return person;
            } else {
                return tracking;
            }
        }
    }

    public static String parseResult(String message) {
        if (checkJsonEmpty(message)) {
            return "";
        }
        try {
            JSONObject json = new JSONObject(message);
            return json.getString("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RESULT_FAILED;
    }


    public static String parseRegisterName(String params) {
        if (checkJsonEmpty(params)) {
            return "";
        }
        String name = "";
        if (TextUtils.isEmpty(params) || "[]".equals(params) || "{}".equals(params)) {
            return name;
        }

        try {
            JSONObject jsonObject = new JSONObject(params);
            String slots = jsonObject.optString("slots");
            if (TextUtils.isEmpty(slots) || "[]".equals(slots) || "{}".equals(slots)) {
                name = "";
            } else {
                JSONArray start = new JSONObject(slots).optJSONArray("start");
                JSONObject jsonObj = (JSONObject) start.opt(0);
                name = jsonObj.optString("value");
            }
            return name;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static Person parsePerson(String str) {
        Person person;
        Gson gson = new Gson();
        person = gson.fromJson(str, Person.class);
        return person;
    }

    public static String getAnswerText(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        String answerText = "";
        try {
            if (TextUtils.isEmpty(json)) {
                return answerText;
            }
            JSONObject object = new JSONObject(json);
            answerText = object.optString("answerText");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return answerText;
    }

    public static String getUserText(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        String userText = "";
        try {
            JSONObject object = new JSONObject(json);
            userText = object.optString("userText");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userText;
    }

    public static String getDestination(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        String destination = "";
        try {
            JSONObject object = new JSONObject(json);
            JSONObject jsonObject = new JSONObject(object.optString("slots"));
            JSONArray jsonArray = jsonObject.optJSONArray("destination");
            JSONObject info = (JSONObject) jsonArray.get(0);
            if (info != null) {
                destination = info.optString("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return destination;
    }

    public static String getMove(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        String destination = "";
        try {
            JSONObject object = new JSONObject(json);
            JSONObject jsonObject = new JSONObject(object.optString("slots"));
            JSONArray jsonArray = jsonObject.optJSONArray("direction");
            JSONObject info = (JSONObject) jsonArray.get(0);
            if (info != null) {
                destination = info.optString("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return destination;
    }

    public static String getLocation(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        String destination = "";
        try {
            JSONObject object = new JSONObject(json);
            JSONObject jsonObject = new JSONObject(object.optString("slots"));
            JSONArray jsonArray = jsonObject.optJSONArray("location");
            JSONObject info = (JSONObject) jsonArray.get(0);
            if (info != null) {
                destination = info.optString("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return destination;
    }

    public static List<String> getPictures(String json) {
        List<String> pictureList = new ArrayList<>();
        if (checkJsonEmpty(json)) {
            return pictureList;
        }
        try {
            JSONObject object = new JSONObject(json);
            String status = object.optString("status");
            if ("ok".equals(status)) {
                JSONArray jsonArray = object.optJSONArray("pictures");
                for (int i = 0; i < jsonArray.length(); i++) {
                    pictureList.add(jsonArray.getString(i));
                }
                return pictureList;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return pictureList;
    }

    public static String getPersonName(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        try {
            JSONObject object = new JSONObject(json);
            String status = object.optString("message");
            if ("ok".equals(status)) {
                return object.optString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * {"code":-5,"message":"not found user"}
     * {"code":-5,"message":"{\"image\": [{\"message\": \"This field is required.\", \"code\": \"required\"}]}"}
     *
     * @param json
     * @return
     */
    public static int getPersonError(String json) {
        if (checkJsonEmpty(json)) {
            return 1;
        }
        try {
            JSONObject object = new JSONObject(json);
            String status = object.optString("message");
            if ("not found user".equals(status)) {
                return 1;
            } else {
                return 2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getRegisterRemoteName(String json) {
        if (checkJsonEmpty(json)) {
            return "";
        }
        try {
            JSONObject object = new JSONObject(json);
            String remoteType = object.optString("register_remote_type");
            String remoteName = object.optString("register_remote_name");
            if (Definition.REGISTER_REMOTE_SERVER_EXIST.equals(remoteType)) {
                return "录入成功，你是" + remoteName;
            } else {
                return "录入成功，你是" + remoteName;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static boolean checkJsonEmpty(String value) {
        if (TextUtils.isEmpty(value)) {
            return true;
        }
        return false;
    }

}
