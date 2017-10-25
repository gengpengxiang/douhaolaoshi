package com.bj.eduteacher.api;

import android.util.Base64;

import com.bj.eduteacher.entity.AppVersionInfo;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.BadgeType;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.ClassInfo;
import com.bj.eduteacher.entity.ClassItemInfo;
import com.bj.eduteacher.entity.ClassNewsInfo;
import com.bj.eduteacher.entity.CommentInfo;
import com.bj.eduteacher.entity.KidClassInfo;
import com.bj.eduteacher.entity.KidDataInfo;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.SubjectInfo;
import com.bj.eduteacher.entity.TeacherInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.utils.CharEncodeUtil;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzeaseui.model.EaseConversation;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.getJsonByPostUrl;

/**
 * Created by he on 2016/11/24.
 */
public class LmsDataService {

    private static final int PAGE_SIZE = 10;

    /**
     * 获取学生的姓名，学校，班级
     *
     * @param kidId
     * @return
     * @throws Exception
     */
    public KidClassInfo getKidClassInfoFromAPI(String kidId) throws Exception {
        KidClassInfo kidClassInfo = new KidClassInfo();
        String parseUrl = "teacher/classinfo/" + kidId;
        LL.i("获取学生信息：" + parseUrl);
        String resultStr = HttpUtilService.getJsonByUrl(parseUrl);
        if (StringUtils.isEmpty(resultStr)) {
            kidClassInfo.setErrorCode("0");
            kidClassInfo.setMessage("班级ID输入有误");
            return kidClassInfo;
        }

        JSONObject result = new JSONObject(resultStr);
        String id = result.optString("id");
        if (StringUtils.isEmpty(id)) {
            kidClassInfo.setErrorCode("0");
            kidClassInfo.setMessage("班级ID输入有误");
            return kidClassInfo;
        } else {
            // 成功的情况下
            kidClassInfo.setKidId(kidId);
            kidClassInfo.setKidName(result.optString("name"));
            kidClassInfo.setKidImg(HttpUtilService.BASE_RESOURCE_URL + result.optString("classpic"));
            kidClassInfo.setSchoolId(result.optString("schoolid"));
            kidClassInfo.setSchoolName(result.optString("school_name"));

            kidClassInfo.setErrorCode("1");
            kidClassInfo.setMessage("成功");
            return kidClassInfo;
        }
    }

    public LmsDataService() {
    }

    public String[] getTeacherInfoFromAPI(String phoneNumber) throws Exception {
        String[] result = new String[3];
        String parseUrl = "teacher/teacherinfo/" + phoneNumber;
        LL.i("获取教师信息：" + parseUrl);
        String resultStr = HttpUtilService.getJsonByUrl(parseUrl);
        if (StringUtils.isEmpty(resultStr)) {
            result[0] = "0";
            result[1] = "没有找到您的信息";
            return result;
        }

        JSONObject resultObj = new JSONObject(resultStr);
        result[0] = "1";
        result[1] = resultObj.optString("name");
        String teacherImg = resultObj.optString("img");
        result[2] = StringUtils.isEmpty(teacherImg) ? "" : teacherImg;

        return result;
    }

    public TeacherInfo getTeacherInfoFromAPI2(String phoneNumber) throws Exception {
        TeacherInfo teacherInfo = new TeacherInfo();
        String parseUrl = "js/getteacherinfo";
        LL.i("获取学生信息：" + parseUrl);
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", phoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            if (dataArray.length() == 0) {
                teacherInfo.setErrorCode("0");
                teacherInfo.setMessage("学生数据为空");
                return teacherInfo;
            }

            JSONObject dataObj = dataArray.optJSONObject(0);
            // 成功的情况下
            teacherInfo.setTeacherNickname(dataObj.optString("nicheng", ""));
            teacherInfo.setTeacherName(dataObj.optString("name"));
            teacherInfo.setTeacherPhoneNumber(phoneNumber);
            teacherInfo.setTeacherImg(StringUtils.isEmpty(dataObj.optString("teacherimg")) ? "" : HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("teacherimg"));
            teacherInfo.setSchoolCode(dataObj.optString("schoolcode"));
            teacherInfo.setSchoolName(dataObj.optString("schoolname"));
            teacherInfo.setSchoolImg(StringUtils.isEmpty(dataObj.optString("schoolimg")) ? "" : HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("schoolimg"));
        } else {
            teacherInfo.setErrorCode("0");
            teacherInfo.setMessage(errorMsg);
        }
        return teacherInfo;
    }

    /**
     * 获取学生的数据
     *
     * @param kidId
     * @return
     * @throws Exception
     */

    public KidDataInfo getKidDataInfoFromAPI(String kidId) throws Exception {
        KidDataInfo kidDataInfo = new KidDataInfo();
        String parseUrl = "student/getdata/" + kidId;
        LL.i("获取学生的数据：" + parseUrl);
        JSONObject resultObj = new JSONObject(HttpUtilService.getJsonByUrl(parseUrl));

        kidDataInfo.setScore(resultObj.optString("score"));
        kidDataInfo.setGrade(resultObj.optString("grade"));
        kidDataInfo.setBadge(resultObj.optString("badge"));
        kidDataInfo.setPingyu(resultObj.optString("pingyu"));
        kidDataInfo.setUpdateTime(resultObj.optString("updatetime"));

        kidDataInfo.setErrorCode("1");
        kidDataInfo.setMessage("成功");

        return kidDataInfo;
    }

    /**
     * 获取验证码
     *
     * @return
     * @throws Exception
     */
    public String[] getCodeFromAPI(String phoneNumber) throws Exception {
        String[] result = new String[2];
        String parseUrl = HttpUtilService.BASE_URL + "dayumg/teachersendMsg.php?phone=" + phoneNumber;
        LL.i("获取验证码：" + parseUrl);
        JSONObject resultObject = new JSONObject(HttpUtilService.getJsonBycompletelyUrl(parseUrl));
        if (resultObject.has("result")) {
            String errorCode = resultObject.optString("result");
            String messaage = resultObject.optString("error");
            result[0] = errorCode;
            result[1] = messaage;
        } else {
            result[0] = "0";
            result[1] = "数据异常，请重试";
        }
        return result;
    }

    public String[] getCodeFromAPI2(String phoneNumber) throws Exception {
        String[] result = new String[2];
        String parseUrl = HttpUtilService.BASE_URL + "dayumg/jssendmsg.php";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber);

        LL.i("获取验证码：" + parseUrl);
        JSONObject resultObject = new JSONObject(HttpUtilService.getJsonByPostCompleteUrl(parseUrl, params));

        result[0] = resultObject.optString("ret");
        result[1] = resultObject.optString("msg");

        return result;
    }

    /**
     * 登录
     *
     * @return
     * @throws Exception
     */
    public String[] loginFromAPI(String phoneNumber, String code) throws Exception {
        String[] result = new String[2];
        String parseUrl = "teacher/login/" + phoneNumber + "/" + code;
        LL.i("登录：" + parseUrl);
        JSONObject resultObject = new JSONObject(HttpUtilService.getJsonByUrl(parseUrl));

        if (resultObject.has("result")) {
            String errorCode = resultObject.optString("result");
            String messaage = resultObject.optString("error");
            result[0] = errorCode;
            result[1] = messaage;
        } else {
            result[0] = "0";
            result[1] = "数据异常，请重试";
        }
        return result;
    }

    public TeacherInfo loginFromAPI2(String phoneNumber, String code) throws Exception {
        TeacherInfo user = new TeacherInfo();

        String[] result = new String[2];
        String parseUrl = "js/login";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", phoneNumber);
        params.put("yzm", code);

        LL.i("登录：" + parseUrl);
        JSONObject resultObject = new JSONObject(getJsonByPostUrl(parseUrl, params));

        String errorCode = resultObject.optString("ret");
        String errorMsg = resultObject.optString("msg");
        String data = resultObject.optString("data");

        user.setErrorCode(errorCode);
        user.setMessage(errorMsg);

        if (!StringUtils.isEmpty(errorCode) && "1".equals(errorCode) &&
                !StringUtils.isEmpty(data)) {
            // 获取账号直播的相关信息
            JSONObject resultObj = new JSONObject(data);

            user.setSxbStatus(resultObj.optString("sxbstatus"));
            user.setSxbPermissions(resultObj.optString("zhiboquan"));
            user.setSxbUser(resultObj.optString("sxbuser"));
            user.setSxbTitle(resultObj.optString("suixinbotitle"));
            String sxbPic = resultObj.optString("suixinbocover");
            if (!StringUtils.isEmpty(sxbPic)) {
                sxbPic = HttpUtilService.BASE_RESOURCE_URL + sxbPic;
            }
            user.setSxbPicture(sxbPic);
        }

        return user;
    }

    /**
     * 检查是否关联学生
     *
     * @return
     * @throws Exception
     */
    public String[] checkRelationKidFromAPI(String phoneNumber) throws Exception {
        String[] result = new String[3];
        String parseUrl = "teacher/getlink/" + phoneNumber;
        LL.i("检查是否关联学生：" + parseUrl);
        String resultStr = HttpUtilService.getJsonByUrl(parseUrl);
        JSONArray resultArray = new JSONArray(resultStr);
        if (resultArray.length() == 0) {
            result[0] = "2";
            result[1] = "没有关联班级";
            return result;
        }
        JSONObject resultObject = resultArray.getJSONObject(0);
        if (resultObject.has("classcode")) {
            result[0] = "1";
            result[1] = resultObject.optString("classcode");
            result[2] = resultObject.optString("class_name");
        } else {
            result[0] = "0";
            result[1] = "服务器开小差了，请重试";
        }
        return result;
    }


    /**
     * 关联学生
     *
     * @return
     * @throws Exception
     */
    public String[] relationKidFromAPI(String phoneNumber, String kidId) throws Exception {
        String[] result = new String[2];
        // String parseUrl = "teacher/linkclass/" + phoneNumber + "/" + kidId;
        String parseUrl = "js/linkclass";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", kidId);
        params.put("teacherphone", phoneNumber);

        LL.i("关联学生：" + parseUrl);
        JSONObject resultObject = new JSONObject(getJsonByPostUrl(parseUrl, params));

        if (resultObject.has("ret")) {
            String errorCode = resultObject.optString("ret").equals("1") ? "1" : "0";
            String messaage = resultObject.optString("msg");
            result[0] = errorCode;
            result[1] = messaage;
        } else {
            result[0] = "0";
            result[1] = "数据异常，请重试";
        }
        return result;
    }

    /**
     * 上传学生图片
     *
     * @param kidId
     * @param filePath
     * @return
     * @throws Exception
     */
    public String[] uploadKidPhoto(String kidId, String filePath) throws Exception {
        String[] result = new String[2];
        // String parseUrl = "files/index/" + kidId;
        String parseUrl = "js/timg";
        LL.i("上传学生图片：" + parseUrl);
        String resultStr = HttpUtilService.postPictureByUrl(parseUrl, filePath, kidId);

        JSONObject resultObj = new JSONObject(resultStr);

        if (resultObj.has("ret")) {
            String errorCode = resultObj.optString("ret");
            result[0] = errorCode;

            if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
                result[1] = HttpUtilService.BASE_RESOURCE_URL + resultObj.optJSONObject("data").optString("img");
            } else {
                result[0] = "0";
                result[1] = resultObj.optString("msg");
            }
        } else {
            result[0] = "0";
            result[1] = "上传失败";
        }
        return result;
    }

    public String getQRCodeFormWeb(String qrCode) throws Exception {
        String resultStr = HttpUtilService.getJsonBycompletelyUrl(qrCode);
        String code = resultStr.substring(resultStr.indexOf("var jump_url=") + 14,
                resultStr.indexOf("window.location.href") - 4);
        LL.i("解析结果：" + code);
        String result = HttpUtilService.getJsonBycompletelyUrl(code);
        String resultCode = result.substring(result.indexOf("cliinserthtml") + 15,
                result.indexOf("cliinserthtml") + 39);
        if (!StringUtils.checkQRCode(resultCode)) {
            resultCode = result.substring(result.indexOf("data-original-title=\"\">") + 23,
                    result.indexOf("data-original-title=\"\">") + 47);
        }
        LL.i("解析结果：" + resultCode);

        return resultCode;
    }

    /**
     * 计分
     *
     * @param kidId
     * @param code
     * @return
     * @throws Exception
     */
    public String[] addKidScore(String kidId, String code) throws Exception {
        String[] result = new String[4];
        String parseUrl = "scoring/index/" + kidId + "/" + code;
        LL.i("计分：" + parseUrl);
        String resultStr = HttpUtilService.getJsonByUrl(parseUrl);
        JSONObject resultObj = new JSONObject(resultStr);
        if (resultObj.has("status")) {
            result[0] = "1";
            result[1] = resultObj.optString("status");
            result[2] = resultObj.optString("type");
            result[3] = resultObj.optString("value");
        } else {
            result[0] = "0";
            result[1] = "卡片数据有误";
        }
        return result;
    }

    public List<ClassItemInfo> getClassInfoFromAPI(String classId, String pageIndex) throws Exception {
        List<ClassItemInfo> dataList = new ArrayList<>();

        String parseUrlClassScore = "teacher/classdata/" + classId;
        String parseUrlClassStudent = "teacher/cstudents/" + classId + "/" + PAGE_SIZE + "/" + String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE);

        if (pageIndex.equals("1")) {
            String resultClassScoreStr = HttpUtilService.getJsonByUrl(parseUrlClassScore);
            JSONObject resultClassScoreObj = new JSONObject(resultClassScoreStr);

            ClassItemInfo clas = new ClassItemInfo();
            clas.setShowType(ClassItemInfo.SHOWTYPE_CLASS);

            clas.setClasImg(HttpUtilService.BASE_RESOURCE_URL + resultClassScoreObj.optString("classpic"));
            clas.setClasScoreNum(resultClassScoreObj.optString("class_score"));
            clas.setClasBadgeNum(resultClassScoreObj.optString("class_badge"));
            clas.setClasRankNum(resultClassScoreObj.optString("class_ranking"));

            clas.setClasGradeNum1(resultClassScoreObj.optString("class_grade_1"));
            clas.setClasGradeNum2(resultClassScoreObj.optString("class_grade_2"));
            clas.setClasGradeNum3(resultClassScoreObj.optString("class_grade_3"));
            clas.setClasGradeNum4(resultClassScoreObj.optString("class_grade_4"));
            clas.setClasGradeNum5(resultClassScoreObj.optString("class_grade_5"));

            clas.setClasGradeName1(resultClassScoreObj.optString("grade_1_name"));
            clas.setClasGradeName2(resultClassScoreObj.optString("grade_2_name"));
            clas.setClasGradeName3(resultClassScoreObj.optString("grade_3_name"));
            clas.setClasGradeName4(resultClassScoreObj.optString("grade_4_name"));
            clas.setClasGradeName5(resultClassScoreObj.optString("grade_5_name"));

            dataList.add(clas);
        }

        String resultClassStudentInfoStr = HttpUtilService.getJsonByUrl(parseUrlClassStudent);
        JSONArray resultClassStudentArray = new JSONArray(resultClassStudentInfoStr);
        ClassItemInfo student;
        for (int i = 0; i < resultClassStudentArray.length(); i++) {
            JSONObject resultStudentObj = resultClassStudentArray.optJSONObject(i);

            student = new ClassItemInfo();
            student.setShowType(ClassItemInfo.SHOWTYPE_STUDENT);

            student.setStudName(resultStudentObj.optString("name"));
            student.setStudImg(HttpUtilService.BASE_RESOURCE_URL + resultStudentObj.optString("img"));
            student.setStudScore(resultStudentObj.optString("score"));
            student.setStudBadge(resultStudentObj.optString("badge"));
            student.setStudGrade(resultStudentObj.optString("grade"));

            dataList.add(student);
            student = null;
        }
        return dataList;
    }

    public ClassItemInfo getClassDataFromAPI(String classId, String teacherPhone) throws Exception {
        String parseUrlClassScore = "js/classdata";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("teacherphone", teacherPhone);

        String result = getJsonByPostUrl(parseUrlClassScore, params);

        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        ClassItemInfo clas = new ClassItemInfo();
        clas.setErrorCode(errorCode);
        clas.setMessage(errorMsg);

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONObject resultClassScoreObj = new JSONObject(data);

            clas.setShowType(ClassItemInfo.SHOWTYPE_CLASS);

            // clas.setClasImg(HttpUtilService.BASE_RESOURCE_URL + resultClassScoreObj.optString("classpic"));
            clas.setClasImg(HttpUtilService.BASE_RESOURCE_URL + resultClassScoreObj.optString("teacher_pic"));
            clas.setClasScoreNum(resultClassScoreObj.optString("class_dianzan"));
            clas.setClasBadgeNum(resultClassScoreObj.optString("class_badge"));
            clas.setClasBadgeProNum(resultClassScoreObj.optString("class_zhuanxiang"));
            clas.setClasRankNum(resultClassScoreObj.optString("class_ranking"));
            clas.setClasGanXieNum(resultClassScoreObj.optString("class_ganxie"));
            clas.setClasXueke(resultClassScoreObj.optString("teacher_xueke").trim());

            clas.setClasGradeNum1(resultClassScoreObj.optString("class_grade_1"));
            clas.setClasGradeNum2(resultClassScoreObj.optString("class_grade_2"));
            clas.setClasGradeNum3(resultClassScoreObj.optString("class_grade_3"));
            clas.setClasGradeNum4(resultClassScoreObj.optString("class_grade_4"));
            clas.setClasGradeNum5(resultClassScoreObj.optString("class_grade_5"));

            clas.setClasGradeName1(resultClassScoreObj.optString("grade_1_name"));
            clas.setClasGradeName2(resultClassScoreObj.optString("grade_2_name"));
            clas.setClasGradeName3(resultClassScoreObj.optString("grade_3_name"));
            clas.setClasGradeName4(resultClassScoreObj.optString("grade_4_name"));
            clas.setClasGradeName5(resultClassScoreObj.optString("grade_5_name"));
        }
        return clas;
    }

    public List<ClassItemInfo> getClassAllStudentFromAPI(String classId, String type,
                                                         String pageIndex, String orderby) throws Exception {
        List<ClassItemInfo> dataList = new ArrayList<>();
        if (type.equals("0")) {
            String parseUrlClassStudent = "js/cstudents";
            HashMap<String, String> params = new HashMap<>();
            params.put("classcode", classId);
            params.put("limit", String.valueOf(PAGE_SIZE));
            params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));
            params.put("orderby", orderby);

            String result = getJsonByPostUrl(parseUrlClassStudent, params);
            JSONObject resultObj = new JSONObject(result);
            String errorCode = resultObj.optString("ret");
            String errorMsg = resultObj.optString("msg");
            String data = resultObj.optString("data");

            if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
                JSONArray resultClassStudentArray = new JSONArray(data);
                ClassItemInfo student;
                for (int i = 0; i < resultClassStudentArray.length(); i++) {
                    JSONObject resultStudentObj = resultClassStudentArray.optJSONObject(i);

                    student = new ClassItemInfo();
                    student.setShowType(ClassItemInfo.SHOWTYPE_STUDENT);
                    // student.setStudId(resultStudentObj.optString("pid"));
                    student.setStudId(resultStudentObj.optString("pid"));
                    student.setStudName(resultStudentObj.optString("name"));
                    student.setStudImg(HttpUtilService.BASE_RESOURCE_URL + resultStudentObj.optString("img"));
                    student.setStudScore(resultStudentObj.optString("score"));
                    student.setStudBadge(resultStudentObj.optString("badge"));
                    student.setStudGrade(resultStudentObj.optString("grade"));
                    student.setStudPingyu(resultStudentObj.optString("pingyu"));
                    dataList.add(student);
                    student = null;
                }
            }
        } else {
            String parseUrlClassStudent = "js/cgstudentdata";
            HashMap<String, String> params = new HashMap<>();
            params.put("classcode", classId);
            params.put("grade", type);
            params.put("limit", String.valueOf(PAGE_SIZE));
            params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

            String result = getJsonByPostUrl(parseUrlClassStudent, params);
            JSONObject resultObj = new JSONObject(result);
            String errorCode = resultObj.optString("ret");
            String errorMsg = resultObj.optString("msg");
            String data = resultObj.optString("data");

            if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
                JSONArray resultClassStudentArray = new JSONArray(data);
                ClassItemInfo student;
                for (int i = 0; i < resultClassStudentArray.length(); i++) {
                    JSONObject resultStudentObj = resultClassStudentArray.optJSONObject(i);

                    student = new ClassItemInfo();
                    student.setShowType(ClassItemInfo.SHOWTYPE_STUDENT);
                    // student.setStudId(resultStudentObj.optString("pid"));
                    student.setStudId(resultStudentObj.optString("pid"));
                    student.setStudName(resultStudentObj.optString("name"));
                    student.setStudImg(HttpUtilService.BASE_RESOURCE_URL + resultStudentObj.optString("img"));
                    student.setStudScore(resultStudentObj.optString("score"));
                    student.setStudBadge(resultStudentObj.optString("badge"));
                    student.setStudGrade(resultStudentObj.optString("grade"));
                    student.setStudPingyu(resultStudentObj.optString("pingyu"));
                    dataList.add(student);
                    student = null;
                }
            }
        }
        return dataList;
    }

    public List<ClassNewsInfo> getClassAllNewsFromAPI(String classId, String pageIndex) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/classzxdt";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("dongtai_id"));
                    item.setNewsPicture(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("dongtai_pic"));
                    item.setNewsType(itemObj.optString("dongtai_type"));
                    item.setNewsTitle(itemObj.optString("dongtai_title"));
                    item.setNewsTime(itemObj.optString("dongtai_time"));
                    item.setNewsThanksStatus(itemObj.optString("dongtai_ganxiestatus"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("student_pic"));
                    item.setStudentName(itemObj.optString("student_name"));
                    item.setStudentId(itemObj.optString("student_pid"));
                    item.setNewsDesc(itemObj.optString("huizhang_shuoming", ""));
                    dataList.add(item);

                    item = null;
                }
            }
        }

        return dataList;
    }

    public List<ClassNewsInfo> getStudentAllNewsFromAPI(String studentId, String pageIndex, String studentPhoto) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "jz/zxdt";
        HashMap<String, String> params = new HashMap<>();
        params.put("studentid", studentId);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("dongtai_id"));
                    item.setNewsPicture(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("dongtai_pic"));
                    item.setNewsType(itemObj.optString("dongtai_type"));
                    item.setNewsTitle(itemObj.optString("dongtai_title"));
                    item.setNewsTime(itemObj.optString("dongtai_time"));
                    item.setNewsThanksStatus(itemObj.optString("dongtai_ganxiestatus"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("teacher_pic"));
                    item.setStudentName(itemObj.optString("teacher_name"));
                    item.setNewsDesc(itemObj.optString("huizhang_shuoming", ""));
                    dataList.add(item);

                    item = null;
                }
            }
        }

        return dataList;
    }

    public String getCommendReasonFromAPI(String teacherPhoneNumber) throws Exception {
        String parseUrl = "js/dzliyou";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", teacherPhoneNumber);
        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        return data;
    }

    public String[] commendStudentFromAPI(String teacherPhone, String studentId, String reasonId, String position) throws Exception {
        LL.i("点赞接口：teacherPhoneNumber" + teacherPhone);
        String parseUrl = "js/dianzan";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", teacherPhone);
        params.put("studentid", studentId);
        params.put("liyou", reasonId);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");

        String[] commendResult = new String[3];
        commendResult[0] = errorCode;
        commendResult[1] = errorMsg;
        commendResult[2] = position;

        return commendResult;
    }

    /**
     * 新版本的获取学生数据的接口
     *
     * @param studentid
     * @return
     * @throws Exception
     */
    public KidDataInfo getStudentDataFromAPI(String studentid) throws Exception {
        String parseUrlClassScore = "jz/getdata";
        HashMap<String, String> params = new HashMap<>();
        params.put("studentid", studentid);

        String result = getJsonByPostUrl(parseUrlClassScore, params);

        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        KidDataInfo student = new KidDataInfo();
        student.setErrorCode(errorCode);
        student.setMessage(errorMsg);

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONObject resultDataObj = new JSONObject(data);

            student.setScore(resultDataObj.optString("dianzan"));
            student.setBadge(resultDataObj.optString("huizhang"));
            student.setGrade(resultDataObj.optString("dengji"));
            student.setPingyu(resultDataObj.optString("pingyu"));
            student.setBadgePro(resultDataObj.optString("zhuanxiang"));
            student.setUpdateTime(resultDataObj.optString("updatetime"));
        }
        return student;
    }

    public List<ClassInfo> getTeacherLinksClassFromAPI(String teacherID) throws Exception {
        String parseUrl = "js/getlink";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", teacherID);
        params.put("limit", "100");
        params.put("offset", "0");

        String result = getJsonByPostUrl(parseUrl, params);

        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        List<ClassInfo> linkClassList = new ArrayList<>();
        ClassInfo itemClass;
        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject itemObj = dataArray.optJSONObject(i);
                itemClass = new ClassInfo();
                itemClass.setClassID(itemObj.optString("classcode"));
                itemClass.setClassName(itemObj.optString("class_name"));
                itemClass.setTeacherPhoneNumber(itemObj.optString("teacherphone"));
                itemClass.setUpdateTime(itemObj.optString("updatetime"));

                itemClass.setErrorCode(errorCode);
                itemClass.setMessage(errorMsg);

                linkClassList.add(itemClass);
            }
        }
        return linkClassList;
    }

    /**
     * 获取感谢列表
     *
     * @param classId
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ClassNewsInfo> getClassThanksListFromAPI(String classId, String pageIndex) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/cganxies";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("limit", String.valueOf(20));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 20));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("dongtai_id"));
                    item.setNewsTitle(itemObj.optString("dongtai_title"));
                    item.setNewsTime(itemObj.optString("ganxie_time"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("student_pic"));
                    item.setStudentName(itemObj.optString("student_name"));
                    // item.setStudentId(itemObj.optString("student_pid"));
                    dataList.add(item);

                    item = null;
                }
            }
        }
        return dataList;
    }

    /**
     * 获取教师的感谢列表
     *
     * @param teacherPhoneNumber
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ClassNewsInfo> getTeacherThanksListFromAPI(String teacherPhoneNumber, String pageIndex) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/tongzhi";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", teacherPhoneNumber);
        params.put("limit", String.valueOf(15));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 15));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("tongzhi_id"));
                    item.setNewsTitle("感谢了您");
                    item.setNewsTime(itemObj.optString("tongzhi_ganxietime"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("tongzhi_studentimg"));
                    item.setStudentName(itemObj.optString("tongzhi_studentname"));
                    dataList.add(item);

                    item = null;
                }
            }
        }
        return dataList;
    }

    /**
     * 获取未读消息数
     *
     * @return
     * @throws Exception
     */
    public Integer getTeacherUnReadMessageNumberFromAPI(String teacherPhoneNumber) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/zxtongzhi";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", teacherPhoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        Integer unReadMsgNumber;
        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONObject dataObj = new JSONObject(data);
            unReadMsgNumber = Integer.valueOf(dataObj.optString("zxtongzhi_num", "0"));
        } else {
            unReadMsgNumber = 0;
        }
        return unReadMsgNumber;
    }

    /**
     * 徽章类型列表
     *
     * @return
     * @throws Exception
     */
    public List<BadgeType> getClassBadgeTypeListFromAPI(String classId) throws Exception {
        List<BadgeType> badgeTypeList = new ArrayList<>();
        String parseUrl = "js/chznumber";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray badgeTypeArray = new JSONArray(data);
            BadgeType badgeType;
            SubjectInfo xuekeInfo;
            for (int i = 0; i < badgeTypeArray.length(); i++) {
                JSONObject badgeTypeItem = badgeTypeArray.optJSONObject(i);
                badgeType = new BadgeType();
                badgeType.setBadgeTypeID(badgeTypeItem.optString("type_id"));
                badgeType.setName(badgeTypeItem.optString("type_name"));
                JSONArray xuekeArray = badgeTypeItem.optJSONArray("type_xueke");
                List<SubjectInfo> xuekeList = new ArrayList<>();
                for (int j = 0; j < xuekeArray.length(); j++) {
                    JSONObject xuekeObj = xuekeArray.optJSONObject(j);
                    xuekeInfo = new SubjectInfo();
                    xuekeInfo.setSubID(xuekeObj.optString("xueke_id"));
                    xuekeInfo.setSubName(xuekeObj.optString("xueke_name"));
                    xuekeInfo.setSubBadgeCount(Integer.valueOf(xuekeObj.optString("xueke_num")));
                    xuekeList.add(xuekeInfo);
                    xuekeInfo = null;
                    if (j == 0) {
                        badgeType.setNumber(Integer.valueOf(xuekeObj.optString("xueke_num")));
                    }
                }
                badgeType.setXuekeList(xuekeList);
                badgeTypeList.add(badgeType);
                badgeType = null;
            }
        }

        return badgeTypeList;
    }

    /**
     * 获取徽章下的消息列表
     *
     * @param classId
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ClassNewsInfo> getClassBadgeNewsFromAPI(String classId, String badgeTypeId, String xuekeId, String pageIndex) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/chuizhangs";
        LL.i("班级ID -- 徽章类型 -- 学科类型 : " + classId + " -- " + badgeTypeId + " -- " + xuekeId);
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("hztype", badgeTypeId);
        params.put("hzxueke", xuekeId);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("dongtai_id"));
                    item.setNewsPicture(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("dongtai_pic"));
                    item.setNewsType(itemObj.optString("dongtai_type"));
                    item.setNewsTitle(itemObj.optString("dongtai_title"));
                    item.setNewsTime(itemObj.optString("dongtai_time"));
                    item.setNewsThanksStatus(itemObj.optString("dongtai_ganxiestatus"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("student_pic"));
                    item.setStudentName(itemObj.optString("student_name"));
                    item.setStudentId(itemObj.optString("student_pid"));
                    item.setNewsDesc(itemObj.optString("huizhang_shuoming", ""));
                    dataList.add(item);

                    item = null;
                }
            }
        }

        return dataList;
    }

    /**
     * 获取专项下的各分类有的情况
     *
     * @param classId
     * @return
     * @throws Exception
     */
    public List<SubjectInfo> getClassBadgeProTypeFromAPI(String classId) throws Exception {
        List<SubjectInfo> dataList = new ArrayList<>();
        String parseUrl = "js/czxnumber";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optJSONObject("data").optString("8");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray liyouArray = new JSONArray(data);
            SubjectInfo info;
            for (int i = 0; i < liyouArray.length(); i++) {
                JSONObject itemObj = liyouArray.optJSONObject(i);
                info = new SubjectInfo();
                info.setSubID(itemObj.optString("xueke_id"));
                info.setSubName(itemObj.optString("xueke_name"));
                info.setSubBadgeCount(Integer.valueOf(itemObj.optString("xueke_num")));
                dataList.add(info);
                info = null;
            }
        }
        return dataList;
    }

    /**
     * 获取专项徽章下的消息列表
     *
     * @param classId
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ClassNewsInfo> getClassBadgeProNewsFromAPI(String classId, String badgeProTypeID, String pageIndex) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/czhuanxiangs";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("hzxueke", badgeProTypeID);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("dongtai_id"));
                    item.setNewsPicture(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("dongtai_pic"));
                    item.setNewsType(itemObj.optString("dongtai_type"));
                    item.setNewsTitle(itemObj.optString("dongtai_title"));
                    item.setNewsTime(itemObj.optString("dongtai_time"));
                    item.setNewsThanksStatus(itemObj.optString("dongtai_ganxiestatus"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("student_pic"));
                    item.setStudentName(itemObj.optString("student_name"));
                    item.setStudentId(itemObj.optString("student_pid"));
                    item.setNewsDesc(itemObj.optString("huizhang_shuoming", ""));
                    dataList.add(item);

                    item = null;
                }
            }
        }

        return dataList;
    }

    /**
     * 获取点赞下的各种理由有的情况
     *
     * @param classId
     * @return
     * @throws Exception
     */
    public List<SubjectInfo> getClassCommendTypeFromAPI(String classId, String teacherPhone) throws Exception {
        List<SubjectInfo> dataList = new ArrayList<>();
        String parseUrl = "js/cdianzannumber";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("teacherphone", teacherPhone);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optJSONObject("data").optString("z1");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") &&
                !StringUtils.isEmpty(data)) {
            JSONArray liyouArray = new JSONArray(data);
            SubjectInfo info;
            for (int i = 0; i < liyouArray.length(); i++) {
                JSONObject itemObj = liyouArray.optJSONObject(i);
                info = new SubjectInfo();
                info.setSubID(itemObj.optString("liyou_id"));
                info.setSubName(itemObj.optString("liyou_name"));
                info.setSubBadgeCount(Integer.valueOf(itemObj.optString("liyou_num")));
                dataList.add(info);
                info = null;
            }
        }
        return dataList;
    }

    /**
     * 获取点赞下的消息列表
     *
     * @param classId
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ClassNewsInfo> getClassCommendNewsFromAPI(String classId, String teacherPhone, String reasonTypeID, String pageIndex) throws Exception {
        List<ClassNewsInfo> dataList = new ArrayList<>();
        String parseUrl = "js/cdianzans";
        HashMap<String, String> params = new HashMap<>();
        params.put("classcode", classId);
        params.put("teacherphone", teacherPhone);
        params.put("liyou", reasonTypeID);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") &&
                !StringUtils.isEmpty(data)) {
            JSONArray newsArray = new JSONArray(data);
            ClassNewsInfo item;
            if (pageIndex.equals("1") && newsArray.length() == 0) {
                item = ClassNewsInfo.newInstanceForEmptyView();
                dataList.add(item);
            } else {
                for (int i = 0; i < newsArray.length(); i++) {
                    JSONObject itemObj = newsArray.optJSONObject(i);
                    item = new ClassNewsInfo();

                    item.setNewsId(itemObj.optString("dongtai_id"));
                    item.setNewsPicture(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("dongtai_pic"));
                    item.setNewsType(itemObj.optString("dongtai_type"));
                    item.setNewsTitle(itemObj.optString("dongtai_title"));
                    item.setNewsTime(itemObj.optString("dongtai_time"));
                    item.setNewsThanksStatus(itemObj.optString("dongtai_ganxiestatus"));
                    item.setStudentPic(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("student_pic"));
                    item.setStudentName(itemObj.optString("student_name"));
                    item.setStudentId(itemObj.optString("student_pid"));
                    dataList.add(item);

                    item = null;
                }
            }
        }
        return dataList;
    }

    /**
     * 获取版本信息
     *
     * @param appVersion
     * @param appQudao
     * @return
     * @throws Exception
     */
    public AppVersionInfo checkNewVersion(String appVersion, String appQudao) throws Exception {
        AppVersionInfo versionInfo = new AppVersionInfo();
        LL.i("版本号：" + appVersion + " 渠道：" + appQudao);
        String parseUrl = "jz/version";
        HashMap<String, String> params = new HashMap<>();
        params.put("version", appVersion);
        params.put("qudao", appQudao);
        params.put("bao", MLConfig.KEY_APP_PKGNAME);
        params.put("type", MLConfig.KEY_APP_TYPE);
        params.put("os", MLConfig.KEY_APP_OS);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        versionInfo.setErrorCode(errorCode);
        versionInfo.setMessage(errorMsg);

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            versionInfo.setTitle(dataObj.optString("title"));
            versionInfo.setContent(dataObj.optString("content"));
            versionInfo.setDownloadUrl(dataObj.optString("url"));
        } else {

        }

        return versionInfo;
    }

    /**
     * 徽章类型列表
     *
     * @return
     * @throws Exception
     */
    public String[] getSchoolRankListStatusFromAPI(String schoolID) throws Exception {
        String[] resultArray = new String[3];
        String parseUrl = "jzhuoli/tixing";
        HashMap<String, String> params = new HashMap<>();
        params.put("schoolcode", schoolID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        resultArray[0] = errorCode;
        resultArray[1] = errorMsg;

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            resultArray[2] = dataObj.optString("tixing");
        } else {
            resultArray[2] = "0";
        }

        return resultArray;
    }

    /**
     * 获取全校活力榜的不同种类
     *
     * @return
     * @throws Exception
     */
    public List<BadgeType> getSchoolRankListTypesFromAPI(String schoolID) throws Exception {
        List<BadgeType> badgeTypeList = new ArrayList<>();
        String parseUrl = "jzhuoli/shuoming";
        HashMap<String, String> params = new HashMap<>();
        params.put("schoolcode", schoolID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray badgeTypeArray = new JSONObject(data).optJSONArray("type");
            BadgeType badgeType;
            for (int i = 0; i < badgeTypeArray.length(); i++) {
                JSONObject badgeTypeItem = badgeTypeArray.optJSONObject(i);
                badgeType = new BadgeType();
                badgeType.setBadgeTypeID(badgeTypeItem.optString("typeid"));
                badgeType.setName(badgeTypeItem.optString("typename"));
                badgeTypeList.add(badgeType);
                badgeType = null;
            }
        }
        return badgeTypeList;
    }

    /**
     * 获取不同type下的活力榜学生
     *
     * @return
     * @throws Exception
     */
    public List<ClassItemInfo> getSchoolRankListByTypeFromAPI(String badgeTypeId, String schoolID) throws Exception {
        List<ClassItemInfo> dataList = new ArrayList<>();
        String parseUrl = "jzhuoli/huolibang";
        HashMap<String, String> params = new HashMap<>();
        params.put("type", badgeTypeId);
        params.put("schoolcode", schoolID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray newsArray = new JSONArray(data);
            ClassItemInfo item;

            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject itemObj = newsArray.optJSONObject(i);
                item = new ClassItemInfo();

                item.setStudId(itemObj.optString("studentid"));
                item.setStudName(itemObj.optString("studentname"));
                item.setStudImg(HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("studentpic"));
                item.setStudPingyu(itemObj.optString("banji"));
                item.setStudBadge(itemObj.optString("huizhang"));
                item.setStudBadgePro(itemObj.optString("zhuanxiang"));
                item.setStudScore(itemObj.optString("dianzan"));
                item.setStudGrade(itemObj.optString("grade"));

                dataList.add(item);

                item = null;
            }
        }

        return dataList;
    }

    /**
     * 获取逗课列表
     *
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getDouKeListFromAPI(int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();
        String parseUrl = "js/doukelist";
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((pageIndex - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo article;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setArticleID(item.optString("id"));
                article.setTitle(item.optString("title"));
                article.setContent(item.optString("content"));
                article.setAuthor(item.optString("author"));
                article.setPostTime(item.optString("time"));
                article.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("img"));
                article.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("authorimg"));
                article.setArticlePath(item.optString("url"));
                article.setAuthDesc(item.optString("authorjianjie"));
                article.setReadNumber(item.getString("pageview"));
                article.setAgreeNumber(item.getString("dianzan"));
                article.setCommentNumber(item.optString("comment_num", "0"));
                article.setShowType(ArticleInfo.SHOW_TYPE_DOUKE);
                dataList.add(article);
                article = null;
            }
        }
        return dataList;
    }

    /**
     * 文章单次浏览记录 版本：2.1
     *
     * @param newsID
     * @param userPhoneNumber
     * @return
     * @throws Exception
     */
    public BaseDataInfo getArticleReadNumber(String newsID, String userPhoneNumber) throws Exception {
        BaseDataInfo dataInfo = new BaseDataInfo();
        String parseUrl = "jz/newspageview";
        HashMap<String, String> params = new HashMap<>();
        params.put("newsid", newsID);
        params.put("userphone", userPhoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataInfo.setRet(errorCode);
        dataInfo.setMsg(errorMsg);
        dataInfo.setData(data);

        return dataInfo;
    }

    /**
     * 获取逗课详情
     *
     * @param newsID
     * @return
     * @throws Exception
     */
    public ArticleInfo getArticleInfoByID(String newsID) throws Exception {
        ArticleInfo info = new ArticleInfo();

        String parseUrl = "douke/getnewsbyid";
        HashMap<String, String> params = new HashMap<>();
        params.put("newsid", newsID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret", "");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONObject item = new JSONObject(data);
            info.setArticleID(item.optString("id"));
            info.setTitle(item.optString("title"));
            info.setContent(item.optString("content"));
            info.setAuthor(item.optString("author"));
            info.setPostTime(item.optString("time"));
            info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("img"));
            info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("authorimg"));
            info.setArticlePath(item.optString("url"));
            info.setAuthDesc(item.optString("authorjianjie"));
            info.setReadNumber(item.getString("pageview"));
            info.setAgreeNumber(item.getString("dianzan"));
            info.setCommentNumber(item.optString("comment_num", "0"));
        }
        return info;
    }

    /**
     * 为逗课内容点赞或取消点赞 版本：2.1
     *
     * @param newsID
     * @param userPhoneNumber
     * @param type            点赞是1，取消点赞是2，查询是否已点过赞是3
     * @return
     * @throws Exception
     */
    public BaseDataInfo getArticleAgreeNumber(String newsID, String userPhoneNumber, String type) throws Exception {
        BaseDataInfo dataInfo = new BaseDataInfo();
        LL.i("逗课ID：" + newsID + " -- 手机号：" + userPhoneNumber + " -- type：" + type);
        String parseUrl = "jz/newsdianzan";
        HashMap<String, String> params = new HashMap<>();
        params.put("newsid", newsID);
        params.put("userphone", userPhoneNumber);
        params.put("dianzanadd", type);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataInfo.setRet(errorCode);
        dataInfo.setMsg(errorMsg);
        dataInfo.setData(data);

        return dataInfo;
    }

    public List<EaseConversation> getConversationListFromAPI(String userPhone, int pageIndex) throws Exception {
        List<EaseConversation> dataList = new ArrayList<>();
        String parseUrl = "chat/userchatlogs";
        HashMap<String, String> params = new HashMap<>();
        params.put("userphone", userPhone);
        params.put("usertype", MLConfig.KEY_CONVERSATION_TYPE);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((pageIndex - 1) * PAGE_SIZE));

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);

        String errorCode = resultObj.optString("ret", "");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && "1".equals(errorCode)) {
            JSONArray dataArray = new JSONArray(data);
            EaseConversation myConversation;
            EMConversation conversation;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject itemObj = dataArray.optJSONObject(i);
                String parentPhone = itemObj.optString("jiazhang");
                String parentPic = HttpUtilService.BASE_RESOURCE_URL + itemObj.optString("student_img");
                String className = itemObj.optString("class_name");
                String relation = StringUtils.isEmpty(itemObj.optString("jiazhang_juese")) ? "" : "的" + itemObj.optString("jiazhang_juese");
                String parentName = itemObj.optString("student_name") + relation;

                myConversation = new EaseConversation();
                String teacherEaseID = "jiazhang" + parentPhone;
                conversation = EMClient.getInstance().chatManager().getConversation(teacherEaseID, EMConversation.EMConversationType.Chat, true);
                myConversation.setEmConversation(conversation);
                myConversation.setUserEaseID(teacherEaseID);
                myConversation.setUserPhoto(parentPic);
                myConversation.setUserNick(parentName);
                myConversation.setClassName(className);
                myConversation.setRelation(relation);

                dataList.add(myConversation);
                conversation = null;
                myConversation = null;
            }
        }
        return dataList;
    }

    /**
     * 是否显示捐助的入口
     *
     * @return
     * @throws Exception
     */
    public String[] getDonationStatusFromAPI(String type) throws Exception {
        String[] result = new String[3];

        String parseUrl = "pay";
        HashMap<String, String> params = new HashMap<>();
        params.put("appversion", type);

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        result[0] = errorCode;
        result[1] = errorMsg;
        result[2] = data;

        return result;
    }

    /**
     * 生成订单
     *
     * @param price
     * @return
     * @throws Exception
     */
    public OrderInfo getTheOrderInfoFromAPI(String price, String phoneNumber, String type) throws Exception {
        OrderInfo info = new OrderInfo();
        // Wxpayjs/example/Testpay.php?price=3&phone=18911111111
        String parseUrl = "Wxpayjs/example/Testpay.php?price=" + price + "&phone=" + phoneNumber + "&paytype=" + type;
        String resultStr = HttpUtilService.getWxPayJsonByUrl(parseUrl);

        JSONObject resultObj = new JSONObject(resultStr);
        String resultCode = resultObj.optString("result_code", "");
        if ("SUCCESS".equals(resultCode)) {
            info.setAppid(resultObj.optString("appid"));
            info.setMch_id(resultObj.optString("mch_id"));
            info.setNonce_str(resultObj.optString("nonce_str"));
            info.setPrepay_id(resultObj.optString("prepay_id"));
            info.setResult_code(resultCode);
            info.setReturn_code(resultObj.optString("return_code"));
            info.setReturn_msg(resultObj.optString("return_msg"));
            info.setSign(resultObj.optString("sign"));
            info.setTrade_type(resultObj.optString("trade_type"));
            info.setTimeStamp(resultObj.optString("timeStamp"));
            info.setOut_trade_no(resultObj.optString("out_trade_no"));
        }
        return info;
    }

    /**
     * 课程支付
     *
     * @param masterid
     * @param price
     * @param phoneNumber
     * @param type
     * @return
     * @throws Exception
     */
    public OrderInfo getTheOrderInfoFromAPIForCourse(String masterid, String price, String phoneNumber, String type) throws Exception {
        OrderInfo info = new OrderInfo();
        String parseUrl = "Wxpayjs/example/Testpay.php?price=" + price + "&phone=" + phoneNumber + "&paytype=" + type + "&kechengid=" + masterid;
        String resultStr = HttpUtilService.getWxPayJsonByUrl(parseUrl);

        JSONObject resultObj = new JSONObject(resultStr);
        String resultCode = resultObj.optString("result_code", "");
        if ("SUCCESS".equals(resultCode)) {
            info.setAppid(resultObj.optString("appid"));
            info.setMch_id(resultObj.optString("mch_id"));
            info.setNonce_str(resultObj.optString("nonce_str"));
            info.setPrepay_id(resultObj.optString("prepay_id"));
            info.setResult_code(resultCode);
            info.setReturn_code(resultObj.optString("return_code"));
            info.setReturn_msg(resultObj.optString("return_msg"));
            info.setSign(resultObj.optString("sign"));
            info.setTrade_type(resultObj.optString("trade_type"));
            info.setTimeStamp(resultObj.optString("timeStamp"));
            info.setOut_trade_no(resultObj.optString("out_trade_no"));
        }
        return info;
    }

    public OrderInfo getTheOrderInfoFromAPI(String masterid, String price, String phoneNumber, String type) throws Exception {
        OrderInfo info = new OrderInfo();
        // Wxpayjs/example/Testpay.php?price=3&phone=18911111111
        String parseUrl = "Wxpayjs/example/Testpay.php?price=" + price + "&phone=" + phoneNumber + "&paytype=" + type + "&masterresid=" + masterid;
        String resultStr = HttpUtilService.getWxPayJsonByUrl(parseUrl);

        JSONObject resultObj = new JSONObject(resultStr);
        String resultCode = resultObj.optString("result_code", "");
        if ("SUCCESS".equals(resultCode)) {
            info.setAppid(resultObj.optString("appid"));
            info.setMch_id(resultObj.optString("mch_id"));
            info.setNonce_str(resultObj.optString("nonce_str"));
            info.setPrepay_id(resultObj.optString("prepay_id"));
            info.setResult_code(resultCode);
            info.setReturn_code(resultObj.optString("return_code"));
            info.setReturn_msg(resultObj.optString("return_msg"));
            info.setSign(resultObj.optString("sign"));
            info.setTrade_type(resultObj.optString("trade_type"));
            info.setTimeStamp(resultObj.optString("timeStamp"));
            info.setOut_trade_no(resultObj.optString("out_trade_no"));
        }
        return info;
    }

    public OrderInfo getTheOrderInfoFromAPIForLive(String masterid, String sxbroomuser, String price, String phoneNumber, String type) throws Exception {
        OrderInfo info = new OrderInfo();
        // Wxpayjs/example/Testpay.php?price=3&phone=18911111111
        String parseUrl = "Wxpayjs/example/Testpay.php?price=" + price + "&phone=" + phoneNumber + "&paytype=" + type + "&masterresid=" + masterid + "&sxbroomuser=" + sxbroomuser;
        String resultStr = HttpUtilService.getWxPayJsonByUrl(parseUrl);

        JSONObject resultObj = new JSONObject(resultStr);
        String resultCode = resultObj.optString("result_code", "");
        if ("SUCCESS".equals(resultCode)) {
            info.setAppid(resultObj.optString("appid"));
            info.setMch_id(resultObj.optString("mch_id"));
            info.setNonce_str(resultObj.optString("nonce_str"));
            info.setPrepay_id(resultObj.optString("prepay_id"));
            info.setResult_code(resultCode);
            info.setReturn_code(resultObj.optString("return_code"));
            info.setReturn_msg(resultObj.optString("return_msg"));
            info.setSign(resultObj.optString("sign"));
            info.setTrade_type(resultObj.optString("trade_type"));
            info.setTimeStamp(resultObj.optString("timeStamp"));
            info.setOut_trade_no(resultObj.optString("out_trade_no"));
        }
        return info;
    }

    /**
     * 查询订单交易状态
     *
     * @param tradeID
     * @return
     * @throws Exception
     */
    public TradeInfo getTheTradeInfoFromAPI(String tradeID) throws Exception {
        TradeInfo info = new TradeInfo();

        String parseUrl = "Wxpayjs/example/orderquery.php?out_trade_no=" + tradeID;
        String resultStr = HttpUtilService.getWxPayJsonByUrl(parseUrl);

        JSONObject resultObj = new JSONObject(resultStr);
        String resultCode = resultObj.optString("result_code", "");
        if ("SUCCESS".equals(resultCode)) {
            info.setResult_code(resultCode);
            info.setTrade_state(resultObj.optString("trade_state", ""));
        } else {
            info.setResult_code(resultCode);
        }

        return info;
    }

    /**
     * 删除点赞理由
     *
     * @return
     * @throws Exception
     */
    public String[] deleteTeacherCommendReasonFromAPI(String userPhone, String reasonName) throws Exception {
        String[] result = new String[3];

        String parseUrl = "Jsdzliyou/deldzliyou";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", userPhone);
        params.put("dzliyou", reasonName);

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        result[0] = resultObj.optString("ret", "0");
        result[1] = resultObj.optString("msg", "");
        result[2] = resultObj.optString("data", "");

        return result;
    }

    /**
     * 添加点赞理由
     *
     * @param userPhone
     * @param reasonName
     * @return
     * @throws Exception
     */
    public String[] addTeacherCommendReasonFromAPI(String userPhone, String reasonName) throws Exception {
        String[] result = new String[3];

        String parseUrl = "Jsdzliyou/setdzliyou";
        HashMap<String, String> params = new HashMap<>();
        params.put("teacherphone", userPhone);
        params.put("dzliyou", reasonName);

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        result[0] = resultObj.optString("ret", "0");
        result[1] = resultObj.optString("msg", "");
        result[2] = resultObj.optString("data", "");

        return result;
    }

    /**
     * 获取逗课评论
     *
     * @param newsID
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<CommentInfo> getDoukeAllCommentFromAPI(String newsID, String pageIndex) throws Exception {
        List<CommentInfo> dataList = new ArrayList<>();
        String parseUrl = "douke";
        HashMap<String, String> params = new HashMap<>();
        params.put("newsid", newsID);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") &&
                !StringUtils.isEmpty(data)) {
            JSONArray newsArray = new JSONArray(data);
            CommentInfo item;
            String content, userReply;
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject itemObj = newsArray.optJSONObject(i);
                item = new CommentInfo();

                item.setCommID(itemObj.optString("comment_id", ""));
                item.setCommCreaterName(itemObj.optString("user_title", ""));
                item.setCommCreaterNick(itemObj.optString("nicheng", ""));
                item.setCommCreaterPhoto(HttpUtilService.BASE_RESOURCE_URL +
                        itemObj.optString("user_img", ""));
                item.setCommCreateTime(itemObj.optString("createtime", ""));

                // content = itemObj.optString("content", "");
                // byte b[] = Base64.decode(content, Base64.DEFAULT);
                // userReply = new String(b, "utf-8");
                // item.setCommContent(userReply);

                item.setCommContent(CharEncodeUtil.getBase64DecodeContent(itemObj.optString("content", "")));
                item.setCommCreatePhone(itemObj.optString("userphone", ""));
                dataList.add(item);

                item = null;
            }
        }

        return dataList;
    }

    /**
     * 获取资源的评论列表
     *
     * @param newsID
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<CommentInfo> getResourceAllCommentFromAPI(String newsID, String pageIndex) throws Exception {
        List<CommentInfo> dataList = new ArrayList<>();
        String parseUrl = "ziyuan/index";
        HashMap<String, String> params = new HashMap<>();
        params.put("ziyuanid", newsID);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") &&
                !StringUtils.isEmpty(data)) {
            JSONArray newsArray = new JSONArray(data);
            CommentInfo item;
            String content, userReply;
            for (int i = 0; i < newsArray.length(); i++) {
                JSONObject itemObj = newsArray.optJSONObject(i);
                item = new CommentInfo();

                item.setCommID(itemObj.optString("comment_id", ""));
                item.setCommCreaterName(itemObj.optString("user_title", ""));
                item.setCommCreaterNick(itemObj.optString("nicheng", ""));
                item.setCommCreaterPhoto(HttpUtilService.BASE_RESOURCE_URL +
                        itemObj.optString("user_img", ""));
                item.setCommCreateTime(itemObj.optString("createtime", ""));

                content = itemObj.optString("content", "");
                byte b[] = Base64.decode(content, Base64.DEFAULT);
                userReply = new String(b, "utf-8");

                item.setCommContent(userReply);
                item.setCommCreatePhone(itemObj.optString("userphone", ""));
                dataList.add(item);

                item = null;
            }
        }

        return dataList;
    }

    /**
     * 发送评论内容
     *
     * @param newsID
     * @param userPhoneNumber
     * @param userType
     * @param content
     * @return
     * @throws Exception
     */
    public String[] postDoukeCommentFromAPI(String newsID, String userPhoneNumber,
                                            String userType, String content) throws Exception {
        String[] result = new String[3];

        String userReply = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);

        String parseUrl = "douke/setcomment";
        HashMap<String, String> params = new HashMap<>();
        params.put("newsid", newsID);
        params.put("userphone", userPhoneNumber);
        params.put("usertype", userType);
        params.put("content", userReply);

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        result[0] = errorCode;
        result[1] = errorMsg;
        result[2] = data;

        return result;
    }

    public String[] postResourceCommentFromAPI(String newsID, String userPhoneNumber,
                                               String userType, String content) throws Exception {
        String[] result = new String[3];

        String userReply = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);

        String parseUrl = "ziyuan/setcomment";
        HashMap<String, String> params = new HashMap<>();
        params.put("ziyuanid", newsID);
        params.put("userphone", userPhoneNumber);
        params.put("usertype", userType);
        params.put("content", userReply);

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        result[0] = errorCode;
        result[1] = errorMsg;
        result[2] = data;

        return result;
    }

    /**
     * 获取首页推荐的内容
     *
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getBannerInfoFromAPI() throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/sylunbo";
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "huodong");

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if ("1".equals(errorCode)) {
            if (!StringUtils.isEmpty(data)) {
                JSONArray dataArray = new JSONArray(data);
                ArticleInfo article;
                for (int i = 0; i < dataArray.length(); i++) {
                    article = new ArticleInfo();
                    JSONObject item = dataArray.optJSONObject(i);
                    article.setArticleID(item.optString("code", "")); // 记录ID（逗课ID、资源ID、专家ID）
                    article.setContent(item.optString("title", ""));
                    article.setTitle(item.optString("type", ""));     // 记录type（）
                    article.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));    // 记录展示图片（首页显示的）
                    article.setArticlePath(item.optString("previewurl", ""));       // 记录预览地址（文档、逗课地址）
                    article.setAuthor(HttpUtilService.BASE_RESOURCE_URL + item.optString("downloadurl", ""));            // 记录下载地址（特指文档）

                    dataList.add(article);
                    article = null;
                }
            } else {
                throw new Exception("未获取到数据");
            }
        } else if ("2".equals(errorCode)) {
            // 无数据
            throw new Exception("暂无数据");
        } else {
            // 参数错误
            throw new Exception(errorMsg);
        }
        return dataList;
    }

    /**
     * 获取专家列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterCardsFromAPI(int pageIndex, int pageSize) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/mastercard";
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(pageSize));
        params.put("offset", String.valueOf((pageIndex - 1) * pageSize));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo article;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setArticleID(item.optString("mastercode", ""));
                article.setAuthor(item.optString("name", ""));
                article.setTitle(item.optString("title", ""));
                article.setAuthDesc(item.optString("sjianjie", ""));
                article.setContent(item.optString("news", ""));
                article.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                article.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA);
                dataList.add(article);
                article = null;
            }
        }
        return dataList;
    }

    /**
     * 获取名师列表
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getFamousTeacherCardsFromAPI(int pageIndex, int pageSize) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/mingshicard";
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(pageSize));
        params.put("offset", String.valueOf((pageIndex - 1) * pageSize));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo article;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setArticleID(item.optString("mastercode", ""));
                article.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                article.setAuthor(item.optString("name", ""));
                article.setTitle(item.optString("xuekeshengfen", ""));
                article.setContent(item.optString("newupdate", ""));
                article.setShowType(ArticleInfo.SHOW_TYPE_TEACHER);
                dataList.add(article);
                article = null;
            }
        }
        return dataList;
    }

    /**
     * 获取专家总数
     *
     * @return
     * @throws Exception
     */
    public ArticleInfo getMasterCountFromAPI() throws Exception {
        ArticleInfo articleInfo = new ArticleInfo();

        String parseUrl = "jsmaster/masternum";
        HashMap<String, String> params = new HashMap<>();

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            articleInfo.setReplyCount(dataObj.optString("masternum", ""));
        }
        return articleInfo;
    }

    /**
     * 获取首页每日一课列表
     *
     * @return
     * @throws Exception
     * @setAuthDesc top bottom center single
     */
    public List<ArticleInfo> getHomePageLatestRes(String phoneNumber) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "kecheng/mryk";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("code", ""));
                info.setTitle(item.optString("title", ""));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买

                if (dataArray.length() == 1) {
                    info.setAuthDesc("single");
                } else {
                    if (i == 0) {
                        info.setAuthDesc("top");
                    } else if (i == dataArray.length() - 1) {
                        info.setAuthDesc("bottom");
                    } else {
                        info.setAuthDesc("center");
                    }
                }
                info.setPreviewType(item.optString("type", ""));
                info.setArticlePath(item.optString("previewurl", ""));       // 记录预览地址（文档、逗课地址）
                info.setAuthor(HttpUtilService.BASE_RESOURCE_URL + item.optString("downloadurl", ""));            // 记录下载地址（特指文档）

                info.setShowType(ArticleInfo.SHOW_TYPE_LATEST_RES);
                dataList.add(info);
            }
        }
        return dataList;
    }

    /**
     * 获取首页名师成长课程列表
     *
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getHomePageCourseList(String phoneNumber) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "kecheng/kechenglist";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("title", ""));
                info.setAuthor(item.optString("author", ""));
                info.setReadNumber(item.optString("keshi", ""));
                info.setReplyCount(StringUtils.isEmpty(item.optString("studentnum", "0")) ? "0" : item.optString("studentnum", "0"));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
                // 简介、课程说明和证书
                info.setAuthDesc(item.optString("jianjie", ""));
                info.setContent(item.optString("shuoming", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("zhengshu", ""));

                info.setShowType(ArticleInfo.SHOW_TYPE_COURSE);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 获取名师总数
     *
     * @return
     * @throws Exception
     */
    public ArticleInfo getFamousTeacherCountFromAPI() throws Exception {
        ArticleInfo articleInfo = new ArticleInfo();

        String parseUrl = "jsmaster/mingshinum";
        HashMap<String, String> params = new HashMap<>();

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            articleInfo.setReplyCount(dataObj.optString("masternum", ""));
        }
        return articleInfo;
    }

    /**
     * 获取专家信息接口
     *
     * @param masterID
     * @return
     * @throws Exception
     */
    public ArticleInfo getMasterCardsFromAPI(String masterID) throws Exception {
        ArticleInfo masterInfo = new ArticleInfo();

        String parseUrl = "jsmaster/masterinfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("mastercode", masterID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONObject dataObj = new JSONObject(data);
            masterInfo.setAuthor(dataObj.optString("name", ""));
            masterInfo.setTitle(dataObj.optString("title", ""));
            masterInfo.setAuthDesc(dataObj.optString("jianjie", ""));
            masterInfo.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("img", ""));
            masterInfo.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("bgimg", ""));
        }
        return masterInfo;
    }

    /**
     * 获取名师信息
     *
     * @param masterID
     * @return
     * @throws Exception
     */
    public ArticleInfo getFamousTeacherFromAPI(String masterID) throws Exception {
        ArticleInfo masterInfo = new ArticleInfo();

        String parseUrl = "jsmaster/masterinfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("mastercode", masterID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONObject dataObj = new JSONObject(data);
            masterInfo.setAuthor(dataObj.optString("name", ""));
            masterInfo.setTitle(dataObj.optString("xuekeshengfen", ""));
            masterInfo.setAuthDesc(dataObj.optString("jianjie", ""));
            masterInfo.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("img", ""));
            masterInfo.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("bgimg", ""));
        }
        return masterInfo;
    }

    /**
     * 获取逗课的基本信息：课表说明、专家解读
     *
     * @param masterID
     * @return
     * @throws Exception
     */
    public ArticleInfo getNewDoukeInfoFromAPI(String masterID) throws Exception {
        ArticleInfo masterInfo = new ArticleInfo();

        String parseUrl = "jsmaster/kthemeinfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("kthemeid", masterID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONObject dataObj = new JSONObject(data);
            masterInfo.setAuthor(dataObj.optString("name", ""));
            masterInfo.setTitle(dataObj.optString("shuoming", ""));
            masterInfo.setAuthDesc(dataObj.optString("jiedu", ""));
        }
        return masterInfo;
    }

    /**
     * 获取专家资源列表
     *
     * @param masterID
     * @param phoneNumber
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterResFromAPI(String masterID, String phoneNumber, int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/masterres";
        HashMap<String, String> params = new HashMap<>();
        params.put("mastercode", masterID);
        params.put("phone", phoneNumber);
        params.put("type", "all");
        params.put("limit", String.valueOf(100));
        params.put("offset", String.valueOf((pageIndex - 1) * 100));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("title", ""));
                info.setAuthor(item.optString("filename", ""));
                info.setArticlePath(item.optString("previewurl", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("fileurl", ""));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
                info.setReplyCount(item.optString("viewnum", "0"));
                info.setPreviewType(item.optString("type", "0"));

                info.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_RES);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 获取课程下的资源ID
     *
     * @param courseID
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getCourseResFromAPI(String courseID) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "kecheng/kechengres";
        HashMap<String, String> params = new HashMap<>();
        params.put("kechengid", courseID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("title", ""));
                info.setAuthor(item.optString("filename", ""));
                info.setArticlePath(item.optString("previewurl", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("fileurl", ""));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
                info.setReplyCount(item.optString("viewnum", "0"));
                info.setPreviewType(item.optString("type", "0"));

                info.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_RES);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 获取课程的详细信息
     *
     * @param courseID
     * @param phoneNumber
     * @return
     * @throws Exception
     */
    public ArticleInfo getCourseDetailInfoFromAPI(String courseID, String phoneNumber) throws Exception {
        ArticleInfo info = new ArticleInfo();
        String parseUrl = "kecheng/kecheng";
        HashMap<String, String> params = new HashMap<>();
        params.put("kechengid", courseID);
        params.put("phone", phoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONObject item = new JSONObject(data);

            info.setArticleID(item.optString("id", ""));
            info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
            info.setTitle(item.optString("title", ""));
            info.setAuthor(item.optString("author", ""));
            info.setReadNumber(item.optString("keshi", ""));
            info.setReplyCount(StringUtils.isEmpty(item.optString("studentnum", "0")) ? "0" : item.optString("studentnum", "0"));
            info.setAgreeNumber(item.optString("price", "0"));
            info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
            // 简介、课程说明和证书
            info.setAuthDesc(item.optString("jianjie", ""));
            info.setContent(item.optString("shuoming", ""));
            info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("zhengshu", ""));
            info.setNickname(item.optString("jiakestatus", ""));// 0 未加入，1 已加入

            info.setShowType(ArticleInfo.SHOW_TYPE_COURSE);
        }
        return info;
    }

    /**
     * 获取逗课资源列表
     *
     * @param masterID
     * @param phoneNumber
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getDoukeResFromAPI(String masterID, String phoneNumber, int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/kthemeres";
        HashMap<String, String> params = new HashMap<>();
        params.put("kthemeid", masterID);
        params.put("userphone", phoneNumber);
        params.put("type", "all");
        params.put("limit", String.valueOf(100));
        params.put("offset", String.valueOf((pageIndex - 1) * 100));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("title", ""));
                info.setAuthor(item.optString("filename", ""));
                info.setArticlePath(item.optString("previewurl", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("fileurl", ""));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
                info.setReplyCount(item.optString("viewnum", "0"));
                info.setPreviewType(item.optString("type", "0"));

                info.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_RES);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 获取首页专家日课
     *
     * @param phoneNumber
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterRikeFromAPI(String phoneNumber) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/masterrike";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phoneNumber);
        params.put("type", "all");
        params.put("limit", "9");
        params.put("offset", "0");

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("title", ""));
                info.setAuthor(item.optString("filename", ""));
                info.setArticlePath(item.optString("previewurl", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("fileurl", ""));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
                info.setPreviewType(item.optString("type", "0"));
                info.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_RES);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 获取专家逗课列表
     *
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterDouKeListFromAPI(String masterID, int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();
        String parseUrl = "jsmaster/masterdouke";
        HashMap<String, String> params = new HashMap<>();
        params.put("mastercode", masterID);
        params.put("limit", String.valueOf(50));
        params.put("offset", String.valueOf((pageIndex - 1) * 50));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo article;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setArticleID(item.optString("doukeid", ""));
                article.setTitle(item.optString("title", ""));
                article.setContent(item.optString("jianjie", ""));
                article.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                article.setAuthor(item.optString("mastertag", ""));
                article.setArticlePath(item.optString("url", ""));

                article.setShowType(ArticleInfo.SHOW_TYPE_DOUKE);
                dataList.add(article);
                article = null;
            }
        }
        return dataList;
    }

    /**
     * 获取专家黑板报的内容
     *
     * @param masterID
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterSubjectTopFromAPI(String masterID, int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/mastertopic";
        HashMap<String, String> params = new HashMap<>();
        params.put("mastercode", masterID);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((pageIndex - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            if (dataArray == null) {
                return dataList;
            }
            ArticleInfo subjectTopInfo;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                subjectTopInfo = new ArticleInfo();

                String subID = item.optString("id", "");
                subjectTopInfo.setArticleID(subID);
                subjectTopInfo.setTitle(item.optString("title", ""));
                subjectTopInfo.setContent(item.optString("content", ""));

                // 获取精品回复
                ArticleInfo topReplys = getMasterSubjectTopReplyFromAPI(subID);
                subjectTopInfo.setReplyCount(topReplys.getReplyCount());
                subjectTopInfo.setReplyList(topReplys.getReplyList());

                dataList.add(subjectTopInfo);
                subjectTopInfo = null;
            }
        }
        return dataList;
    }

    /**
     * 获取专家话题列表
     *
     * @param masterID
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterSubjectsFromAPI(String masterID, int pageIndex) throws Exception {

        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/mastertopicreply";
        HashMap<String, String> params = new HashMap<>();
        params.put("mastercode", masterID);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((pageIndex - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            if (dataArray == null) {
                return dataList;
            }
            ArticleInfo item, rjReply;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject itemObj = dataArray.optJSONObject(i);
                item = new ArticleInfo();
                item.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_MORE);
                item.setArticleID(itemObj.optString("id", ""));
                item.setTitle(itemObj.optString("title", ""));
                item.setContent(itemObj.optString("content", ""));

                JSONObject replyCountObj = itemObj.optJSONObject("replynum");
                if (replyCountObj != null) {
                    item.setReplyCount(replyCountObj.optString("countall", "0"));
                }

                JSONArray tjReplyArray = itemObj.optJSONArray("tuijianreply");
                if (tjReplyArray != null && tjReplyArray.length() > 0) {
                    JSONObject tjReplyObj = tjReplyArray.optJSONObject(0);
                    List<ArticleInfo> replyList = new ArrayList<>();
                    rjReply = new ArticleInfo();
                    rjReply.setPostTime(tjReplyObj.optString("updatetime", ""));

                    String content = tjReplyObj.optString("content", "");
                    byte b[] = Base64.decode(content, Base64.DEFAULT);
                    String userReply = new String(b, "utf-8");

                    rjReply.setContent(userReply);
                    rjReply.setAuthor(tjReplyObj.optString("name", ""));
                    rjReply.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + tjReplyObj.optString("img", ""));
                    rjReply.setAuthDesc(tjReplyObj.optString("phone", ""));

                    replyList.add(rjReply);
                    item.setReplyList(replyList);
                    rjReply = null;
                }
                dataList.add(item);
                item = null;
            }
        }
        return dataList;
    }

    /**
     * 获取专家主题的精品回复
     *
     * @param topicId
     * @return
     * @throws Exception
     */
    public ArticleInfo getMasterSubjectTopReplyFromAPI(String topicId) throws Exception {
        ArticleInfo dataInfo = new ArticleInfo();

        List<ArticleInfo> dataList = new ArrayList<>();
        String parseUrl = "jsmaster/topicreplytj";
        HashMap<String, String> params = new HashMap<>();
        params.put("topicid", topicId);
        params.put("limit", "2");
        params.put("offset", "0");

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            String replyCount = (new JSONObject(data)).optJSONObject("replaynum").optString("countall", "");
            dataInfo.setReplyCount(replyCount);

            JSONArray dataArray = (new JSONObject(data)).optJSONArray("replaydata");
            ArticleInfo article;

            if (dataArray == null) {
                return dataInfo;
            }
            String content, userReply;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setPostTime(item.optString("updatetime", ""));

                content = item.optString("content", "");
                byte b[] = Base64.decode(content, Base64.DEFAULT);
                userReply = new String(b, "utf-8");

                article.setContent(userReply);
                article.setAuthor(item.optString("name", ""));
                article.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                article.setAuthDesc(item.optString("phone", ""));
                article.setNickname(item.optString("nicheng", ""));

                dataList.add(article);
                article = null;
            }

            dataInfo.setReplyList(dataList);
        }
        return dataInfo;
    }

    /**
     * 查看回复列表
     *
     * @param topicId
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getMasterSubjectReplyListFromAPI(String topicId, int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();
        String parseUrl = "jsmaster/topicreply";
        HashMap<String, String> params = new HashMap<>();
        params.put("topicid", topicId);
        params.put("limit", String.valueOf(PAGE_SIZE));
        params.put("offset", String.valueOf((pageIndex - 1) * PAGE_SIZE));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = (new JSONObject(data)).optJSONArray("replaydata");

            if (dataArray == null) {
                return dataList;
            }

            ArticleInfo article;
            String content, userReply;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setPostTime(item.optString("updatetime", ""));

                content = item.optString("content", "");
                byte b[] = Base64.decode(content, Base64.DEFAULT);
                userReply = new String(b, "utf-8");

                article.setContent(userReply);
                article.setAuthor(item.optString("name", ""));
                article.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                article.setAuthDesc(item.optString("phone", ""));
                article.setNickname(item.optString("nicheng", ""));

                dataList.add(article);
                article = null;
            }
        }
        return dataList;
    }

    /**
     * 新增回复内容
     *
     * @param userPhoneNumber
     * @param content
     * @return
     * @throws Exception
     */
    public String[] postSubjectReplyFromAPI(String topicid, String userPhoneNumber, String content) throws Exception {
        String[] result = new String[3];

        String userReply = Base64.encodeToString(content.getBytes(), Base64.DEFAULT);

        String parseUrl = "jsmaster/addreply";
        HashMap<String, String> params = new HashMap<>();
        params.put("topicid", topicid);
        params.put("phone", userPhoneNumber);
        params.put("replycontent", userReply);

        String resultStr = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(resultStr);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        result[0] = errorCode;
        result[1] = errorMsg;
        result[2] = data;

        return result;
    }

    /**
     * 获取逗课的学科列表
     *
     * @return
     * @throws Exception
     */
    public List<String> getNewDoukeXuekeFromAPI() throws Exception {
        List<String> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/kthemexk";
        HashMap<String, String> params = new HashMap<>();

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            for (int i = 0; i < dataArray.length(); i++) {
                dataList.add(dataArray.optJSONObject(i).optString("xueke"));
            }
        }

        return dataList;
    }

    /**
     * 增加阅读量
     *
     * @param masterresid
     * @throws Exception
     */
    public String[] addResourcePreviewNumber(String masterresid) throws Exception {
        String[] dataList = new String[3];
        String parseUrl = "jsmaster/resvnumadd";
        HashMap<String, String> params = new HashMap<>();
        params.put("masterresid", masterresid);

        String result = HttpUtilService.getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataList[0] = errorCode;
        dataList[1] = errorMsg;

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            String readNum = dataObj.optString("affected_rows", "0");
            dataList[2] = readNum;
        } else {
            dataList[2] = "0";
        }

        return dataList;
    }

    /**
     * 获取资源的点赞数量和评论数量
     *
     * @param resID
     * @return
     * @throws Exception
     */
    public ArticleInfo getResInfoByID(String resID) throws Exception {
        ArticleInfo resInfo = new ArticleInfo();

        String parseUrl = "ziyuan/getbyid";
        HashMap<String, String> params = new HashMap<>();
        params.put("ziyuanid", resID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONObject info = new JSONObject(data);
            String likeNum = info.optString("dianzan", "0");
            String commentNum = info.optString("comment_num", "0");
            String readNum = info.optString("viewnum", "0");

            resInfo.setReadNumber(StringUtils.isEmpty(readNum) ? "0" : readNum);
            resInfo.setAgreeNumber(StringUtils.isEmpty(likeNum) ? "0" : likeNum);
            resInfo.setCommentNumber(StringUtils.isEmpty(commentNum) ? "0" : commentNum);
        }

        return resInfo;
    }

    /**
     * 获取资源点赞状态
     *
     * @return
     * @throws Exception
     */
    public BaseDataInfo getResourceAgreeStatus(String newsID, String userPhoneNumber, String caozuo) throws Exception {
        BaseDataInfo info = new BaseDataInfo();

        String parseUrl = "ziyuan/dianzan";
        HashMap<String, String> params = new HashMap<>();
        params.put("ziyuanid", newsID);
        params.put("userphone", userPhoneNumber);
        params.put("caozuo", caozuo);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        info.setRet(errorCode);
        info.setMsg(errorMsg);
        info.setData(data);

        return info;
    }

    /**
     * 获取新逗课的列表
     *
     * @param xueke
     * @param nianji
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getNewDoukeListFromAPI(String xueke, String nianji, int offset, int pageSize) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jsmaster/ktheme";
        HashMap<String, String> params = new HashMap<>();
        params.put("xueke", xueke);
        params.put("nianji", nianji);
        params.put("type", "all");
        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(pageSize));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("name", ""));
                info.setAgreeNumber(item.optString("keshi", "0"));
                info.setCommentNumber(item.optString("krnum", "0"));

                info.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_RES);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 清空教师关联的所有班级的数据
     *
     * @param phone
     * @return
     * @throws Exception
     */
    public String[] clearAllClassData(String phone) throws Exception {
        String[] resultArray = new String[3];

        String parseUrl = "jslishidata/setbyphone";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", phone);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            resultArray[0] = errorCode;
            resultArray[1] = "清除成功，请切换至班级栏刷新页面";
        } else {
            resultArray[0] = "0";
            resultArray[1] = "清除失败，请稍后重试或联系我们";
        }
        return resultArray;
    }

    /**
     * 获取直播列表
     *
     * @param teacherPhoneNumber
     * @param type               默认值为1返回值为正在进行的直播，传值为2时返回已结束的直播
     * @param limit
     * @param offset
     * @return
     */
    public List<ArticleInfo> getLiveListFromAPI(String teacherPhoneNumber, String type, int limit, int offset) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "jssuixinbo/sxbrooms";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", teacherPhoneNumber);
        params.put("type", type);
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")
                && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();

                info.setArticleID(item.optString("id", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("cover", ""));
                info.setTitle(item.optString("title", ""));

                info.setAuthDesc(item.optString("uid", ""));
                info.setAuthor(item.optString("uid_name", ""));
                info.setNickname(item.optString("uid_nicheng", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("uid_img", ""));

                info.setPostTime(item.optString("create_time", "0"));
                info.setFinishTime(item.optString("finish_time", "0"));

                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("paystatus", "0")); // 0 未购买，1 已购买
                info.setPreviewType(type);
                info.setShowType(ArticleInfo.SHOW_TYPE_LIVE);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 更新房间信息
     *
     * @param phoneNumber
     * @param roomid
     * @param title
     * @param picUrl
     * @return
     * @throws Exception
     */
    public String[] notifyNewRoomInfoFromAPI(String phoneNumber, int roomid, String title, String picUrl) throws Exception {
        String[] dataList = new String[3];

        String parseUrl = "jssuixinbo/updateroominfo";
        HashMap<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("cover", picUrl);
        params.put("roomid", String.valueOf(roomid));
        params.put("phone", phoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataList[0] = errorCode;
        dataList[1] = errorMsg;
        dataList[2] = data;

        return dataList;
    }

    public String[] updateUserNickName(String teacherPhoneNumber, String nickname) throws Exception {
        String[] dataList = new String[3];

        String parseUrl = "nicheng/index";
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", teacherPhoneNumber);
        params.put("nicheng", nickname);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataList[0] = errorCode;
        dataList[1] = errorMsg;
        dataList[2] = data;

        return dataList;
    }

    public String[] joinCourseFromAPI(String courseID, String phoneNumber, String payStatus) throws Exception {
        String[] dataList = new String[3];

        String parseUrl = "kecheng/kechengjr";
        HashMap<String, String> params = new HashMap<>();
        params.put("kechengid", courseID);
        params.put("phone", phoneNumber);
        params.put("paystatus", payStatus);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataList[0] = errorCode;
        dataList[1] = errorMsg;
        dataList[2] = data;

        return dataList;
    }

    /**
     * 获取活动的相关信息
     *
     * @param huodongID
     * @return
     * @throws Exception
     */
    public ArticleInfo getHuodongInfoFromAPI(String huodongID) throws Exception {
        ArticleInfo info = new ArticleInfo();

        String parseUrl = "anli/huodong";
        HashMap<String, String> params = new HashMap<>();
        params.put("huodongid", huodongID);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            info.setArticleID(dataObj.optString("id", ""));
            info.setTitle(dataObj.optString("title", ""));
            info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + dataObj.optString("banner", ""));
        }
        return info;
    }

    /**
     * 案例列表
     *
     * @param huodongID
     * @param phone
     * @param author
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getAnliListFromAPI(String huodongID, String phone, String author,
                                                int pageIndex, int pageSize) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "anli/anlilist";
        HashMap<String, String> params = new HashMap<>();
        params.put("huodongid", huodongID);
        params.put("phone", phone);
        params.put("author", author);
        params.put("limit", String.valueOf(pageSize));
        params.put("offset", String.valueOf((pageIndex - 1) * pageSize));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1") && !StringUtils.isEmpty(data)) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo article;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                article = new ArticleInfo();
                article.setArticleID(item.optString("id", ""));
                article.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                article.setTitle(item.optString("title", ""));
                article.setAuthor(item.optString("author", ""));
                article.setContent(item.optString("diqu", ""));
                article.setAgreeNumber(StringUtils.isEmpty(item.optString("piaonum", "")) ? "0" : item.optString("piaonum", ""));
                article.setCommentNumber(item.optString("atpstatus", ""));
                dataList.add(article);
                article = null;
            }
        }
        return dataList;
    }

    /**
     * 获取案例下的所有资源
     *
     * @param anliid
     * @param phoneNumber
     * @param pageIndex
     * @return
     * @throws Exception
     */
    public List<ArticleInfo> getAnliResFromAPI(String anliid, String phoneNumber, int pageIndex) throws Exception {
        List<ArticleInfo> dataList = new ArrayList<>();

        String parseUrl = "anli/anlireslist";
        HashMap<String, String> params = new HashMap<>();
        params.put("anliid", anliid);
        params.put("phone", phoneNumber);
        params.put("limit", String.valueOf(100));
        params.put("offset", String.valueOf((pageIndex - 1) * 100));

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && errorCode.equals("1")) {
            JSONArray dataArray = new JSONArray(data);
            ArticleInfo info;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.optJSONObject(i);
                info = new ArticleInfo();
                info.setArticleID(item.optString("id", ""));
                info.setAuthImg(HttpUtilService.BASE_RESOURCE_URL + item.optString("img", ""));
                info.setTitle(item.optString("title", ""));
                info.setAuthor(item.optString("filename", ""));
                info.setArticlePath(item.optString("previewurl", ""));
                info.setArticlePicture(HttpUtilService.BASE_RESOURCE_URL + item.optString("fileurl", ""));
                info.setAgreeNumber(item.optString("price", "0"));
                info.setCommentNumber(item.optString("buystatus", "0")); // 0 未购买，1 已购买
                info.setReplyCount(item.optString("viewnum", "0"));
                info.setPreviewType(item.optString("type", "0"));

                info.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_RES);
                dataList.add(info);
            }
        }

        return dataList;
    }

    /**
     * 案例投票功能
     *
     * @param newsID
     * @param userPhoneNumber
     * @param type
     * @return
     * @throws Exception
     */
    public BaseDataInfo getAnliToupiaoFromAPI(String newsID, String userPhoneNumber, String type) throws Exception {
        BaseDataInfo dataInfo = new BaseDataInfo();
        String parseUrl = "anli/anlitoupiao";
        HashMap<String, String> params = new HashMap<>();
        params.put("anliid", newsID);
        params.put("phone", userPhoneNumber);
        params.put("caozuo", type);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        dataInfo.setRet(errorCode);
        dataInfo.setMsg(errorMsg);
        dataInfo.setData(data);

        return dataInfo;
    }

    /**
     * 案例投票查询功能
     *
     * @param anliID
     * @param phoneNumber
     * @return
     * @throws Exception
     */
    public BaseDataInfo searchAnliToupiaoFromAPI(String anliID, String phoneNumber) throws Exception {
        BaseDataInfo dataInfo = new BaseDataInfo();
        String parseUrl = "anli/anlitoupiao";
        HashMap<String, String> params = new HashMap<>();
        params.put("anliid", anliID);
        params.put("phone", phoneNumber);

        String result = getJsonByPostUrl(parseUrl, params);
        JSONObject resultObj = new JSONObject(result);
        String errorCode = resultObj.optString("ret");
        String errorMsg = resultObj.optString("msg");
        String data = resultObj.optString("data");

        if (!StringUtils.isEmpty(errorCode) && "2".equals(errorCode) && !StringUtils.isEmpty(data)) {
            JSONObject dataObj = new JSONObject(data);
            String atpStatus = dataObj.optString("atpstatus", "");
            String msg = dataObj.optString("msg", "");
            String num = StringUtils.isEmpty(dataObj.optString("piaonum", "")) ? "0" : dataObj.optString("piaonum", "");

            dataInfo.setRet(atpStatus);
            dataInfo.setMsg(msg);
            dataInfo.setData(num);
        } else {
            dataInfo.setRet(errorCode);
            dataInfo.setMsg(errorMsg);
            dataInfo.setData(data);
        }
        return dataInfo;
    }
}
