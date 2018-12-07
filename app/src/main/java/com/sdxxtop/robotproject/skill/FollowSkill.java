package com.sdxxtop.robotproject.skill;

import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.actionbean.LeadingParams;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.listener.PersonInfoListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.xuxin.entry.ReserveBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FollowSkill {
    public static final String TAG = "FollowSkill";
    private static FollowSkill followSkill;

    private CurrentLocation mCurLocation;
    private Timer mAvoidTimer;
    private int mLeadReqId = 0;
    private int avoidCount;
    private StopSuccessListener mStopSuccessListener;
    private LeadStatus mLeadStatus;
    private LeadInfo mLeadInfo;
    private MyPersonInfoListener myPersonInfoListener;
    private Timer mLostTimer;
    private boolean mIsRelocationing;
    private ReserveBean.DataBean dataBean;

    private FollowSkill() {
        mCurLocation = null;
        mLeadInfo = new LeadInfo();
        myPersonInfoListener = new MyPersonInfoListener();
    }

    public static FollowSkill getInstance() {
        if (followSkill == null) {
            followSkill = new FollowSkill();
        }
        return followSkill;
    }

    private static String logFormat(int var0, String var1) {
        Object[] var2 = new Object[]{"FollowSkill", var0, var1};
        return String.format("%s [reqId=%d] %s", var2);
    }


    public void prepareLead(ReserveBean.DataBean dataBean) {
        Log.e(TAG, "prepareLead: dataBean = " + dataBean.getPlace());
        this.dataBean = dataBean;
        myPersonInfoListener.setDataBean(dataBean);
        mLeadInfo.mDestination = dataBean.getPlace();
        mLeadStatus = LeadStatus.FINDING;
        RobotApi.getInstance().stopGetAllPersonInfo(0, null);
        RobotApi.getInstance().startGetAllPersonInfo(0, myPersonInfoListener);
    }

    private class MyPersonInfoListener extends PersonInfoListener {
        ReserveBean.DataBean dataBean;

        public void setDataBean(ReserveBean.DataBean dataBean) {
            this.dataBean = dataBean;
        }

        @Override
        public void onResult(int status, String responseString) {
            super.onResult(status, responseString);
            Log.e(TAG, "onResult: status = " + status + " , responseString = " + responseString);
        }

        @Override
        public void onData(int code, List<Person> data) {
            super.onData(code, data);
//            Log.e(TAG, "onData111: " + data);
            if (data != null && data.size() > 0) {
//                Person person = data.get(0);
//                Log.e(TAG, "onData: " + person.toGson());
//                handleFindingPerson(data);
                if (mLeadStatus == LeadStatus.FINDING) {
                    handleFindingPerson(data);
                } else if (mLeadStatus == LeadStatus.LEAD_WAIT) {
                    handleLeadWaitState(data);
                }
            }
        }
    }

    private void handleLeadWaitState(List<Person> personList) {
        Person person = this.getSpecialIdPerson(personList);
        if (person != null) {
            double distance = person.getDistance();
            if (distance <= 0.0D || distance >= 2.5999999999999996D) {
                this.startLostTimer(1000L, 0);
                return;
            }

            Log.e(TAG, "handleLeadWaitState guest near. mLeadStatus= " + this.mLeadStatus + " || distance=" + distance);
            RobotApi.getInstance().stopGetAllPersonInfo(0, myPersonInfoListener);
            this.guestFarawayRestart(0);
            this.destroyLostTimer();
        }

    }

    private Person getSpecialIdPerson(List<Person> personList) {
        if (personList != null && personList.size() != 0) {
            Iterator iterator = personList.iterator();

            Person person;
            do {
                if (!iterator.hasNext()) {
                    return null;
                }
                person = (Person) iterator.next();
            } while (this.mLeadInfo.mPersonId != person.getId());

            return person;
        } else {
            return null;
        }
    }

    private void handleFindingPerson(List<Person> personList) {
        Person person = this.getWinPerson(personList);
        if (person != null) {
            Log.e(TAG, "handleFindingPerson winPerson.Id:" + person.getId() + " || name:" + person.getName());
            this.mLeadInfo.mPersonId = person.getId();
            this.mLeadInfo.mPersonName = person.getName();
//            this.mISafetyCheckNew.personFound();
//            this.cancelCheckPersonTimer();
            RobotApi.getInstance().stopGetAllPersonInfo(0, myPersonInfoListener);
            FaceSkill.getInstance().stopFocusFollow();

            startSpeakLead();
        }

    }

    private void startSpeakLead() {
        if (dataBean != null) {
            playTxt(dataBean.getBegin_word(), new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    startLead(0, false, false);
                }
            });
        } else {
            startLead(0, false, false);
        }
    }

    public void startLead(final int reqId, final boolean isDelay, final boolean restart) {
        Log.e(TAG, logFormat(reqId, "startLead name = " + mLeadInfo.getName() + " personId = " + mLeadInfo.getPersonId() + " destination = " + mLeadInfo.getDestination()));
        this.mLeadStatus = FollowSkill.LeadStatus.LEADING;
        String destination = mLeadInfo.getDestination();
        LeadingParams loadingParams = new LeadingParams();
        loadingParams.setPersonId(mLeadInfo.getPersonId());
        loadingParams.setCustomerName(mLeadInfo.getName());
        loadingParams.setDestinationName(mLeadInfo.getDestination());
        loadingParams.setLostTimer(1000L);
        long delayTime;
        if (isDelay) {
            delayTime = 0L;
        } else {
            delayTime = 5000L;
        }

        loadingParams.setDetectDelay(delayTime);
        loadingParams.setAvoidTimeout(30000L);
        loadingParams.setAvoidDistance(2.0D);
        loadingParams.setWaitTimeout(43200000L);
        loadingParams.setMaxDistance(2.8D);
        final RobotApi var16 = RobotApi.getInstance();
        var16.startLead(reqId, loadingParams, new ActionListener() {
            public void onError(int errorCode, String errorString) throws RemoteException {
                Log.e(TAG, logFormat(reqId, "startLead :: onError " + errorCode + " || " + errorString));
                switch (errorCode) {
                    case -117:
                    case -107:
//                        mISafetyCheckNew.noPerson();
                        return;
                    case -116:
                        playTTS("请先定位");
//                        release(reqId, 101, (Bundle)null);
                        release();
                        return;
                    case -113:
                        playTTS("这里就是" + destination + "地点了");
//                        release(reqId, 100, (Bundle)null);
                        release();
                        return;
                    case -109:
                        boolean isDestination = "接待点".equals(destination);
                        String speechValue;
                        if (mCurLocation != null && mCurLocation.isNear2Destination) {
                            if (isDestination) {
                                speechValue = "前面就是目的地了";
                            } else {
                                speechValue = "前面就是" + destination + ",我先回去了";
                            }

//                            FollowSkill var10 = FollowSkill.this;
//                            EMOJI_TYPE[] var11 = new EMOJI_TYPE[]{EMOJI_TYPE.TYPE_SAY2, EMOJI_TYPE.TYPE_SAY1};
//                            var10.playEmojies(var11);
                        } else {
                            if (isDestination) {
                                speechValue = "过不去了，过不去了";
                            } else {
                                speechValue = "过不去了，我先回去了";
                            }

//                            FollowSkill var8 = FollowSkill.this;
//                            EMOJI_TYPE[] var9 = new EMOJI_TYPE[]{EMOJI_TYPE.TYPE_SAY1, EMOJI_TYPE.TYPE_SAY1, EMOJI_TYPE.TYPE_PATHETIC};
//                            var8.playEmojies(var9);
                        }

                        playTTS(speechValue);
                        leadCompleted(reqId);
                        return;
                    case -108:
                        playTTS("很抱歉，我不知道" + destination + "在哪");
//                        release(reqId, 101, (Bundle)null);
                        release();
                        return;
                    case -105:
                        var16.switchCamera(reqId, "forward", (CommandListener) null);
//                        FollowSkill var4 = FollowSkill.this;
//                        EMOJI_TYPE[] var5 = new EMOJI_TYPE[]{EMOJI_TYPE.TYPE_SAY3, EMOJI_TYPE.TYPE_DEPRESS};
//                        var4.playEmojies(var5);
//                        release(reqId, 101, (Bundle)null);
                        release();
                        return;
                    case -1:
                        Log.e(TAG, logFormat(reqId, "startLead :: onError ALREADY_RUN"));
                        return;
                    default:
                        release();
//                        release(reqId, 101, (Bundle)null);
                }
            }

            public void onResult(int status, String responseString) throws RemoteException {
                Log.e(TAG, logFormat(reqId, "startLead :: onResult " + status + " || " + responseString));
                switch (status) {
                    case 1:
//                        leadCompletedVoice(reqId, true);
                        String speechValue = "这里就是目的地了";
                        if (dataBean != null) {
                            speechValue = dataBean.getEnd_word();
                            dataBean = null;
                        }
                        playTxt(speechValue, new TextListener() {
                            @Override
                            public void onComplete() {
                                super.onComplete();
                                navigateToForeground(0);
                            }
                        });
                        return;
                    case 2:
                    default:
                        Log.e(TAG, logFormat(reqId, "startLead::ActionListener::onResult unknown status: " + status + " response: " + responseString));
                        break;
                    case 3:
                        if (mStopSuccessListener != null) {
                            mStopSuccessListener.onStopSuccess();
                            return;
                        }
                }

            }

            public void onStatusUpdate(int status, String data) throws RemoteException {
                Log.e(TAG, logFormat(reqId, "startLead :: onStatusUpdate " + status + " || " + data));
                switch (status) {
                    case 1009:
                        if (mAvoidTimer != null) {
                            Log.e(TAG, FollowSkill.logFormat(reqId, "Do not perform faraway logic when in avoid-stopping state"));
                            return;
                        }

                        mLeadStatus = FollowSkill.LeadStatus.LEAD_WAIT;
                        stopLeadAsync(false, new FollowSkill.StopSuccessListener() {
                            void onStopSuccess() {
                                super.onStopSuccess();
                                guestFarawayWait(reqId);
                            }
                        });
                        return;
                    case 1010:
//                        SkillManager.getInstance().cancelWakeupUi();
                        if (isDelay) {
                            Log.e(TAG, FollowSkill.logFormat(reqId, "startLead :: onStatusUpdate.Now is restart leading."));
                            return;
                        }

                        if (restart) {
                            Log.e(TAG, FollowSkill.logFormat(reqId, "startLead :: onStatusUpdate.Now is restart leading when pause."));
                            return;
                        }

                        startLeadingVoice(destination);
                        if (mCurLocation == null) {
                            mCurLocation = new FollowSkill.CurrentLocation();
                            return;
                        }
                    case 1011:
                    case 1013:
                    case 1014:
                    case 1015:
                    default:
                        break;
                    case 1012:
                        if (mCurLocation != null) {
                            mCurLocation.isNear2Destination = true;
                            return;
                        }
                        break;
                    case 1016:
                    case 1018:
//                        if (mIsRelocationing) {
//                            Log.e(TAG, FollowSkill.logFormat(reqId, "return " + status + " when startLead mIsRelocationing"));
//                            return;
//                        }

                        startAvoidTimer(FollowSkill.LeadStatus.LEADING);
                        return;
                    case 1017:
                    case 1019:
//                        if (mIsRelocationing) {
//                            Log.e(TAG, FollowSkill.logFormat(reqId, "return " + status + " when startLead mIsRelocationing"));
//                            return;
//                        }

                        cancelAvoidTimer();
                        avoidCount = 0;
                        startLead(reqId, true, false);
                        return;
                }

            }
        });
    }

    private void navigateToForeground(final int var1) {
        this.mLeadStatus = LeadStatus.NAVIGATION_RETURN;
        RobotApi.getInstance().resetHead(var1, (CommandListener) null);
        RobotApi.getInstance().startNavigation(var1, "接待点", 0.5D, 30000L, new ActionListener() {
            public void onError(int var1x, String var2) throws RemoteException {
                Log.e(TAG, logFormat(var1, "leadCompleted startNavigation :: onError : " + var1x + " || " + var2));
                switch (var1x) {
                    case -116:
                        playTTS("请先定位");
//                        this.release(var1, 101, (Bundle)null);
                        release();
                        return;
                    case -109:
                        playTTS(mLeadInfo.mDestination + "我回不去了");
//                        LeadingModule.this.playEmoji(EMOJI_TYPE.TYPE_SAY2);
//                        LeadingModule.this.release(var1, 101, (Bundle)null);
                        release();
                        return;
                    case -1:
                        Log.e(TAG, logFormat(var1, "navigateToForeground onError : ALREADY_RUN"));
                        return;
                    default:
                        release();
//                        LeadingModule.this.release(var1, 101, (Bundle)null);
                }
            }

            public void onResult(int var1x, String var2) throws RemoteException {
                Log.e(TAG, "navigationToFrontDesk :: onResult : " + var1x + " || " + var2);
                switch (var1x) {
                    case 1:
//                        LeadingModule.this.release(var1, 100, (Bundle)null);
                        release();
                        mLeadInfo.clearInfo();
                        RobotApi.getInstance().stopNavigation(0);
                        return;
                    case 3:
                        if (mStopSuccessListener != null) {
                            mStopSuccessListener.onStopSuccess();
                            return;
                        }
                    case 2:
                    default:
                }
            }

            public void onStatusUpdate(int var1x, String var2) throws RemoteException {
                Log.e(TAG, logFormat(var1, "leadCompleted startNavigation :: onStatusUpdate : " + var1x + " || " + var2));
                switch (var1x) {
                    case 1016:
                    case 1018:
                        if (mIsRelocationing) {
                            Log.e(TAG, logFormat(var1, "return " + var1x + " when navigateToForeground mIsRelocationing"));
                            return;
                        }

                        startAvoidTimer(LeadStatus.NAVIGATION_RETURN);
                        return;
                    case 1017:
                    case 1019:
                        if (!mIsRelocationing) {
                            cancelAvoidTimer();
                            avoidCount = 0;
                            navigateToForeground(var1);
                            return;
                        }
                    default:
                }
            }
        });
    }

    private void guestFarawayWait(int reqId) {
        Log.e(TAG, logFormat(reqId, "guestFarawayWait..."));
        if (mLeadInfo.mGuestFarawayCounts < 1) {
//            LeadingModule.LeadInfo.access$4008(this.mLeadInfo);
            playTTS("请跟上我");
//            this.playEmoji(EMOJI_TYPE.TYPE_SAY1);
        }

//        this.mPersonInfoListener.setReqId(reqId);
        RobotApi.getInstance().startGetAllPersonInfo(reqId, myPersonInfoListener);
        this.startLostTimer(2000L, reqId);
    }

    private void startLostTimer(long var1, final int var3) {
        this.destroyLostTimer();
        if (this.mLeadStatus == LeadStatus.LEAD_WAIT) {
            Log.d(TAG, "startLostTimer : " + this.mLeadStatus);
            this.mLostTimer = new Timer();
            this.mLostTimer.schedule(new TimerTask() {
                public void run() {
                    if (mLeadStatus == LeadStatus.LEAD_WAIT) {
                        Log.e(TAG, "startLostTimer guest lost. mLeadStatus= " + mLeadStatus);
                        RobotApi.getInstance().stopGetAllPersonInfo(var3, myPersonInfoListener);
                        guestFarawayRestart(var3);
                    }

                }
            }, var1);
        }
    }

    private void guestFarawayRestart(int reqId) {
        Log.e(TAG, logFormat(reqId, "guestFarawayRestart... mLeadStatus= " + this.mLeadStatus));
        if (this.mLeadStatus == LeadStatus.LEAD_WAIT) {
            this.startLead(reqId, true, false);
        }
    }

    private void destroyLostTimer() {
        if (this.mLostTimer != null) {
            this.mLostTimer.cancel();
            this.mLostTimer = null;
        }

    }

    private void stopLeadAsync(boolean isResetHW, StopSuccessListener stopSuccessListener) {
        this.mStopSuccessListener = stopSuccessListener;
        RobotApi.getInstance().stopLead(this.mLeadReqId, isResetHW);
    }

    private Person getWinPerson(List<Person> personList) {
        if (personList != null && personList.size() != 0) {
            if (personList.size() == 1 && this.mLeadInfo.mPersonId != 0 && ((Person) personList.get(0)).getId() != this.mLeadInfo.mPersonId) {
                return null;
            } else if (personList.size() == 1 && this.mLeadInfo.mPersonId != 0 && ((Person) personList.get(0)).getId() == this.mLeadInfo.mPersonId) {
                Log.e(TAG, "getWinPerson ONE Person & special Id: " + this.mLeadInfo.mPersonId);
                return (Person) personList.get(0);
            } else if (personList.size() == 1 && this.mLeadInfo.mPersonId == 0) {
                Log.e(TAG, "getWinPerson ONE Person & NO Id ");
                return (Person) personList.get(0);
            } else {
                if (personList.size() > 1 && this.mLeadInfo.mPersonId != 0) {
                    Iterator var15 = personList.iterator();

                    while (var15.hasNext()) {
                        Person var16 = (Person) var15.next();
                        if (var16.getId() == this.mLeadInfo.mPersonId) {
                            Log.e(TAG, "getWinPerson MULTI Person & special Id: " + this.mLeadInfo.mPersonId);
                            return var16;
                        }
                    }
                }

                if (personList.size() > 1 && this.mLeadInfo.mPersonId == 0) {
                    ArrayList tempList = new ArrayList();
                    double distance = ((Person) personList.get(0)).getDistance();
                    Iterator iterator = personList.iterator();

                    while (iterator.hasNext()) {
                        Person person = (Person) iterator.next();
                        if (person.getDistance() < distance) {
                            distance = person.getDistance();
                            tempList.clear();
                            tempList.add(person);
                        } else if (person.getDistance() == distance) {
                            tempList.add(person);
                        }
                    }

                    if (tempList.size() == 0) {
                        Log.e(TAG, "getWinPerson closest Person & NO Id ");
                        return (Person) tempList.get(0);
                    }

                    if (tempList.size() > 0) {
                        double angle = (double) Math.abs(((Person) personList.get(0)).getAngle());
                        Iterator personIterator = personList.iterator();

                        while (personIterator.hasNext()) {
                            Person person = (Person) personIterator.next();
                            if (angle < (double) Math.abs(person.getAngle())) {
                                double var10000 = (double) Math.abs(person.getAngle());
                                Log.e(TAG, "getWinPerson closest&angleMin Person & NO Id ");
                                return person;
                            }
                        }
                    }
                }

                return null;
            }
        } else {
            return null;
        }
    }

    public class LeadInfo {
        private String mDestination = null;
        private int mGuestFarawayCounts = 0;
        private int mLostCounts = 0;
        private int mPersonId = 0;
        private String mPersonName = null;
        private int sTempPersonNumber = 0;

        public LeadInfo() {
            this.mLostCounts = 0;
            this.mGuestFarawayCounts = 0;
        }

        private String getShortName(String name) {
            if (Pattern.matches("^(R|L)(-)(.+)(-)(.+)?", name)) {
                Matcher matcher = Pattern.compile("[^-]+$").matcher(name);
                if (matcher.find()) {
                    name = matcher.group();
                }
            }

            return name;
        }

        public void clearInfo() {
            this.mPersonName = null;
            this.mPersonId = 0;
            this.mDestination = null;
            this.mLostCounts = 0;
            this.mGuestFarawayCounts = 0;
        }

        public String getDestination() {
            return this.mDestination;
        }

        public String getName() {
            return this.getShortName(this.mPersonName);
        }

        public int getPersonId() {
            return this.mPersonId;
        }

        public String getPersonName() {
            return this.mPersonName;
        }
    }

    public static class CurrentLocation {
        public boolean isManFallow = true;
        public boolean isNear2Destination = false;

        public CurrentLocation() {
        }
    }

    public static enum LeadStatus {
        FINDING,
        IDLE,
        LEADING,
        LEAD_PAUSE,
        LEAD_WAIT,
        NAVIGATION_GO,
        NAVIGATION_PAUSE,
        NAVIGATION_RETURN;

        static {
            LeadStatus[] var0 = new LeadStatus[]{IDLE, FINDING, LEADING, LEAD_PAUSE, LEAD_WAIT, NAVIGATION_GO, NAVIGATION_PAUSE, NAVIGATION_RETURN};
        }

        private LeadStatus() {
        }
    }

    private abstract class StopSuccessListener {
        private StopSuccessListener() {
        }

        void onStopSuccess() {
            mStopSuccessListener = null;
        }
    }

    private void startAvoidTimer(final FollowSkill.LeadStatus var1) {
        this.cancelAvoidTimer();
        Log.e(TAG, logFormat(this.mLeadReqId, "startAvoidTimer leadStatus = " + var1));
        this.mAvoidTimer = new Timer();
        this.mAvoidTimer.schedule(new TimerTask() {
            public void run() {
                avoidCount++;
                Log.i(TAG, logFormat(mLeadReqId, "avoidCount = " + avoidCount));
                if (avoidCount == 1) {
                    Log.i(TAG, logFormat(mLeadReqId, "mAvoidTask SHORT"));
                    playTTS("哎呀，我的路好像被挡住了");
                }

                if (avoidCount == 4) {
                    Log.i(TAG, logFormat(mLeadReqId, "mAvoidTask LONG"));
                    cancelAvoidTimer();
                    avoidCount = 0;
                    avoidFailed(var1);
                }

            }
        }, 5000L, 5000L);
    }

    private void cancelAvoidTimer() {
        Log.e(TAG, logFormat(0, "cancelAvoidTimer"));
        if (this.mAvoidTimer != null) {
            this.mAvoidTimer.cancel();
            this.mAvoidTimer = null;
        }
    }

    private void avoidFailed(LeadStatus var1) {
        Log.e(TAG, logFormat(this.mLeadReqId, "avoidFailed leadStatus=" + var1 + " Destination="));
        switch (var1) {
            case NAVIGATION_RETURN:
                this.playTTS("我回不去了");
//                stopLead(this.mLeadReqId, 1);
                RobotApi.getInstance().stopLead(0, true);
                return;
            case LEADING:
                this.stopLeadAsync(true, new StopSuccessListener() {
                    void onStopSuccess() {
                        super.onStopSuccess();
                        if (mCurLocation != null && mCurLocation.isNear2Destination) {
//                            LeadingModule var5 = this;
                            String var6;
                            if (is2FrontDesk()) {
                                var6 = "前面就是目的地了";
                            } else {
                                var6 = "前面就是" + mLeadInfo.getDestination() + ",我先回去了";
                            }

                            playTTS(var6);
//                            LeadingModule var7 = this;
//                            EMOJI_TYPE[] var8 = new EMOJI_TYPE[]{EMOJI_TYPE.TYPE_SAY2, EMOJI_TYPE.TYPE_SAY1};
//                            var7.playEmojies(var8);
                            leadCompleted(mLeadReqId);
                        } else {
//                            LeadingModule var1 = this;
                            String var2;
                            if (is2FrontDesk()) {
                                var2 = "过不去了，过不去了";
                            } else {
                                var2 = "过不去了，我先回去了";
                            }

                            playTTS(var2);
//                            LeadingModule var3 = this;
//                            EMOJI_TYPE[] var4 = new EMOJI_TYPE[]{EMOJI_TYPE.TYPE_SAY1, EMOJI_TYPE.TYPE_SAY1, EMOJI_TYPE.TYPE_PATHETIC};
//                            var3.playEmojies(var4);
                            leadCompleted(mLeadReqId);
                        }
                    }
                });
                RobotApi.getInstance().stopLead(0, true);
                return;
            case NAVIGATION_GO:
                this.stopNavigationAsync(new StopSuccessListener() {
                    void onStopSuccess() {
                        super.onStopSuccess();
//                    if (this.is2FrontDesk()) {
//                        this.playTTS("我回不去了");
//                        this.playEmoji(EMOJI_TYPE.TYPE_SAY2);
//                        this.release(this.mLeadReqId, 101, (Bundle)null);
                        release();
//                    } else {
//                        this.playTTS("我过不去了，先回去了");
//                        this.leadCompleted(this.mLeadReqId);
//                    }
                    }
                });
                RobotApi.getInstance().stopLead(0, true);
                return;
            default:
        }
    }

    private boolean is2FrontDesk() {
        return this.mLeadInfo != null && "接待点".equals(this.mLeadInfo.getDestination());
    }

    private void stopNavigationAsync(StopSuccessListener var1) {
        this.mStopSuccessListener = var1;
        RobotApi.getInstance().stopNavigation(this.mLeadReqId);
    }

    private void startLeadingVoice(String destination) {
        String text = String.format("好的，马上带你去%s，跟我来吧", destination);
        playTxt(text);
    }

    private void leadCompleted(int var1) {
        Log.e(TAG, logFormat(var1, "leadCompleted..."));
        RobotApi.getInstance().switchCamera(var1, "forward", (CommandListener) null);
        RobotApi.getInstance().stopLead(0, true);
        navigateToForeground(var1);
        release();
//        if (this.is2FrontDesk()) {
//            this.release(var1, 100, (Bundle)null);
//        } else {
//            this.navigateToForeground(var1);
//        }
    }

    private void release() {
        if (mLeadInfo != null) {
            mLeadInfo.clearInfo();
        }
        dataBean = null;
    }

    private void playTTS(String text) {
        playTxt(text);
    }

    private void playTxt(String text) {
        playTxt(text, new TextListener());
    }

    private void playTxt(String text, TextListener textListener) {
        SpeechSkill.getInstance().playTxt(text, new TextListener(){
            @Override
            public void onStart() {
                super.onStart();
                if (textListener != null) {
                    textListener.onStart();
                }
                if (speakingListener != null) {
                    speakingListener.onStart();
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (textListener != null) {
                    textListener.onComplete();
                }
                if (speakingListener != null) {
                    speakingListener.onStop();
                }
            }

            @Override
            public void onStop() {
                super.onStop();
                if (textListener != null) {
                    textListener.onStop();
                }
                if (speakingListener != null) {
                    speakingListener.onStop();
                }
            }

            @Override
            public void onError() {
                super.onError();
                if (textListener != null) {
                    textListener.onError();
                }
                if (speakingListener != null) {
                    speakingListener.onStop();
                }
            }
        });
    }

    private SpeakingListener speakingListener;

    public void setSpeakingListener(SpeakingListener speakingListener) {
        this.speakingListener = speakingListener;
    }

    public interface SpeakingListener {
        void onStart();

        void onStop();
    }
}
