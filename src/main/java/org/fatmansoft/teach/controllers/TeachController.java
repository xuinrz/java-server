package org.fatmansoft.teach.controllers;
//http://127.0.0.1:9090/app/index.html

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class TeachController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();
    static private Integer id;
    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        Student s;
        Map m;
        String courseParas, studentNameParas, scoreParas, logParas, courseManagementParas, honorParas, stuInfParas;

        String practiceParas, dailyParas, tableParas;

        for (int i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            studentNameParas = "model=introduce&studentId=" + s.getId();
            courseParas = "model=course&studentId=" + s.getId();
            logParas = "model=log&studentId=" + s.getId();
            scoreParas = "model=score&studentNum=" + s.getStudentNum();
            courseManagementParas = "model=courseManagement&studentNum=" + s.getStudentNum();
            practiceParas = "model=practice&studentId=" + s.getId();
            honorParas = "model=honor&studentId=" + s.getId();
            dailyParas = "model=daily&studentId=" + s.getId();
            tableParas = "model=table&studentId=" + s.getId();
            stuInfParas = "model=stuInf&stuName=" + s.getStudentName();
            m.put("stuInfParas", stuInfParas);
            m.put("tableParas", tableParas);
            m.put("dailyParas", dailyParas);
            m.put("honorParas", honorParas);
            m.put("practiceParas", practiceParas);
            m.put("scoreParas", scoreParas);
            m.put("logParas", logParas);
            m.put("studentNameParas", studentNameParas);
            m.put("courseManagementParas", courseManagementParas);
            m.put("courseParas", courseParas);
            m.put("id", s.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            if ("男".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex", "男");
            } else {
                m.put("sex", "女");
            }
            m.put("age", s.getAge());
            m.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(), "yyyy-MM-dd"));  //时间格式转换字符串
            m.put("phone", s.getPhone());
            dataList.add(m);
        }
        return dataList;
    }

    //页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //student页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentQuery")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/studentEditInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Student s = null;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if (s != null) {
            form.put("id", s.getId());
            form.put("studentNum", s.getStudentNum());
            form.put("studentName", s.getStudentName());
            form.put("sex", s.getSex().equals("男") ? "男" : "女");  //这里不需要转换
            form.put("age", s.getAge());
            form.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(), "yyyy-MM-dd")); //这里需要转换为字符串
            form.put("phone", s.getPhone());
            form.put("formerSchool", s.getFormerSchool());
            form.put("email", s.getEmail());
            form.put("father", s.getFather());
            form.put("mother", s.getMother());
            form.put("portrait", s.getPortrait());
            if (s.getFace() != null) {
                if (s.getFace().equals("群众")) {
                    form.put("face", "群众");
                } else if (s.getFace().equals("共青团员")) {
                    form.put("face", "共青团员");
                } else if (s.getFace().equals("党员")) {
                    form.put("face", "党员");
                } else {
                    form.put("face", "");
                }
            }
            if (s.getCombination() != null) {
                switch (s.getCombination()) {
                    case "地化生":
                        form.put("combination", "地化生");
                        break;
                    case "地物化":
                        form.put("combination", "地物化");
                        break;
                    case "地物生":
                        form.put("combination", "地物生");
                        break;
                    case "史地化":
                        form.put("combination", "史地化");
                        break;
                    case "史地生":
                        form.put("combination", "史地生");
                        break;
                    case "史化生":
                        form.put("combination", "史化生");
                        break;
                    case "史物化":
                        form.put("combination", "史物化");
                        break;
                    case "史物生":
                        form.put("combination", "史物生");
                        break;
                    case "物化生":
                        form.put("combination", "物化生");
                        break;
                    case "政地物":
                        form.put("combination", "政地物");
                        break;
                    case "政化生":
                        form.put("combination", "政化生");
                        break;
                    case "政史地":
                        form.put("combination", "政史地");
                        break;
                    case "政史化":
                        form.put("combination", "政史化");
                        break;
                    case "政史生":
                        form.put("combination", "政史生");
                        break;
                    case "政史物":
                        form.put("combination", "政史物");
                        break;
                    case "政物化":
                        form.put("combination", "政物化");
                        break;
                    case "政物生":
                        form.put("combination", "政物生");
                        break;
                    case "史地物":
                        form.put("combination", "史地物");
                        break;
                    case "政地生":
                        form.put("combination", "政地生");
                        break;
                    case "20":
                        form.put("combination", "政地物");
                        break;
                    default:
                        form.put("combination", "");
                }
            }


        }
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    @PostMapping("/studentEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        String studentNum = CommonMethod.getString(form, "studentNum");  //Map 获取属性的值
        String studentName = CommonMethod.getString(form, "studentName");
        String sex = CommonMethod.getString(form, "sex");
        Integer age = CommonMethod.getInteger(form, "age");
        Date birthday = CommonMethod.getDate(form, "birthday");
        String phone = CommonMethod.getString(form, "phone");
        String email = CommonMethod.getString(form, "email");
        String formerSchool = CommonMethod.getString(form, "formerSchool");
        String father = CommonMethod.getString(form, "father");
        String mother = CommonMethod.getString(form, "mother");
        String combination = CommonMethod.getString(form, "combination");
        String face = CommonMethod.getString(form, "face");
        String portrait = CommonMethod.getString(form, "portrait");
        Student s = null;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s == null) {
            s = new Student();   //不存在 创建实体对象
            id = studentRepository.getMaxId();  // 查询最大的id
            if (id == null)
                id = 1;
            else
                id = id + 1;
            s.setId(id);  //设置新的id

            if (studentRepository.findByStudentNum(studentNum)!=null)
                return CommonMethod.getReturnMessageError("该学号已存在，无法添加");
            String pattern = "^20(1[6-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9]|6[0-9]|7[0-9]|8[0-9]|9[0-9])\\d{2}[1-9]";//匹配前四位为2016~2099，后三位为001-999的学号。
            if (!Pattern.matches(pattern,studentNum))
                return CommonMethod.getReturnMessageError("学号格式不正确，请重新输入");
        }
        s.setStudentNum(studentNum);  //设置属性
        s.setStudentName(studentName);
        s.setSex(sex);
        s.setAge(age);
        s.setBirthday(birthday);
        s.setPhone(phone);
        s.setEmail(email);
        s.setFormerSchool(formerSchool);
        s.setFather(father);
        s.setMother(mother);
        s.setFace(face);
        s.setCombination(combination);
        s.setPortrait(portrait);
        studentRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/studentDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Student s = null;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            studentRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    //  学生个人简历页面
    //在系统在主界面内点击个人简历，后台准备个人简历所需要的各类数据组成的段落数据，在前端显示
    @PostMapping("/getStudentIntroduceData")
//    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId!=null)id=studentId;
        Map data = introduceService.getIntroduceDataMap(id);
        return CommonMethod.getReturnData(data);  //返回前端个人简历数据
    }

    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.useFastMode();
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf");
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/getStudentIntroducePdf")
    public ResponseEntity<StreamingResponseBody> getStudentIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map data = introduceService.getIntroduceDataMap2(studentId);
//        String content = (String)data.get("html");
//        System.out.println(content.substring(1,10));
//region
        String name = (String) data.get("myName");
        if(name==null)name="";
        String sex = (String) data.get("sex");        if(name==null)name="";
        String portrait = (String) data.get("portrait");        if(portrait==null)portrait="";
        String age = ((Integer) data.get("age")).toString();        if(age==null)age="";
        String birthday = (String) data.get("birthday");        if(birthday==null)birthday="";
        String face = (String) data.get("face");        if(face==null)face="";
        String school = (String) data.get("school");        if(school==null)school="";
        String phone = (String) data.get("phone");        if(phone==null)phone="";
        String email = (String) data.get("email");        if(email==null)email="";
        String gradePoint = (String) data.get("gradePoint");        if(gradePoint==null)gradePoint="";
        List<Score> courseList = (List<Score>) data.get("courseList");
        List<Practice> practiceList = (List<Practice>) data.get("practiceList");
        List<Honor> honorList = (List<Honor>) data.get("honorList");
        // region
        String content = "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'/>" +
                "    <title>简历</title>" +
                "<style> html { font-family: 'SourceHanSansSC', 'Open Sans';}" +
                "th {text-align:center}" +
                "td  {text-align:center}" +
                "</style>" +
                "</head>" +
                "" +
                "" +
                "<body height='1000' background= 'https://img2.baidu.com/it/u=4033026646,1946038390;fm=253'>" +
                "" +
                "" +
                "<table border='1' align='center' cellpadding='1' width='660' height='1000'>" +
                "" +
                "    <tr>" +
                "" +
                "        <th colspan='7' bgcolor='BurlyWood' text-align='center'>个人简介</th>" +
                "" +
                "    </tr>" +
                "" +
                "" +
                "    <tr>" +
                "" +
                "        <th bgcolor='BurlyWood'>姓名:</th>" +
                "" +
                "        <td> " + name + "</td>" +
                "" +
                "        <th bgcolor='BurlyWood'>年龄:</th>" +
                "" +
                "        <td>" + age + "</td>" +
                "" +
                "        <th bgcolor='BurlyWood'>性别:</th>" +
                "" +
                "        <td>"+sex+"</td>" +
                "" +
                "        <td rowspan='3' width='100'>" +
                "" +
                "            <p>" +
                "" +
                "                <a href='https://xuinrz.github.io/'><img border='0' src='https://xuinrz.github.io/Swiper/gogo.png' height='120'/></a>" +
                "" +
                "            </p>" +
                "" +
                "        </td>" +
                "" +
                "    </tr>" +
                "" +
                "" +
                "    <tr>" +
                "" +
                "        <th bgcolor='BurlyWood'>出生日期:</th>" +
                "" +
                "        <td>" + birthday + "</td>" +
                "" +
                "        <th bgcolor='BurlyWood'>政治面貌:</th>" +
                "" +
                "        <td>" + face + "</td>" +
                "" +
                "        <th bgcolor='BurlyWood'>学历:</th>" +
                "" +
                "        <td>" + school + "</td>" +
                "" +
                "    </tr>" +
                "" +
                "" +
                "    <tr>" +
                "" +
                "        <th bgcolor='BurlyWood'>专业:</th>" +
                "" +
                "        <td>山东大学软工</td>" +
                "" +
                "        <th bgcolor='BurlyWood'>邮箱:</th>" +
                "        <td colspan='1'>" +
                "            <p>" +
                "                <a href='mailto:" + email + "'>" + email + " </a>" +
                "            </p>" +
                "        </td>" +
                "        <th bgcolor='BurlyWood'>联系电话:</th>" +
                "" +
                "        <td>" + phone + "</td>" +
                "        </tr>" +
                "" +
                "    <tr>" +
                "" +
                "        <th height='160' bgcolor='BurlyWood'>学习成绩:</th>" +
                "" +
                "        <td colspan='6'>";
        content += "<p>"+"全科平均绩点："+gradePoint+"</p>";

        content += "        </td>" +
                "" +
                "    </tr>" +
                "" +
                "" +
                "    <tr>" +
                "" +
                "        <th height='160' bgcolor='BurlyWood'> 创新实践:</th>" +
                "" +
                "        <td colspan='6'>";
        for (int i = 0; i < practiceList.size(); i++) {
            Practice practice = practiceList.get(i);
            content += "<p>" + practice.getpName() + practice.getpType() + " (指导老师:" + practice.getpTeacher() + ")" + "</p>";
        }
        content += "" + "</td>" +
                "    </tr>" +
                "" +
                "" +
                "    <tr>" +
                "" +
                "        <th height='160' bgcolor='BurlyWood'>获奖经历:</th>" +
                "" +
                "        <td colspan='6'>";
        for (int i = 0; i < honorList.size(); i++) {
            Honor honor =honorList.get(i);
            content += "<p>" + DateTimeTool.parseDateTime(honor.getHonorDate(), "yyyy年MM月dd日 ") + "获" + honor.getHonorLevel() + "奖项：" + honor.getHonorName()+"</p>";
        }
        content += "" + "</td>" +
                "    </tr>" +
                "<tr>" +
                "" +
                "        <th height='160' bgcolor='BurlyWood'>专业课程:</th>" +
                "" +
                "        <td colspan='6'>";
        for (int i = 0; i < courseList.size(); i++) {
            content += "<p>" + courseList.get(i).getCourse().getCourseName() + "</p>";
        }
        content += "" + "</td>" +
                "    </tr>" +
                "" +
                "</table>" +
                "" +
                "</body>" +
                "" +
                "" +
                "</html>";
        //endregion
        return getPdfDataFromHtml(content);
    }


    public List getScoreMapList(String studentId, String courseName) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findByNumNameCourseName(studentId, courseName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
//        String courseParas, studentNameParas;
        for (int i = 0; i < sList.size(); i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            m.put("courseNum", c.getCourseNum());
            m.put("courseName", c.getCourseName());
            m.put("mark", sc.getMark());
            m.put("gradePoint", sc.getGradePoint());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/scoreInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreInit(@Valid @RequestBody DataRequest dataRequest) {
        String studentId = dataRequest.getString("studentNum");
        String courseName = dataRequest.getString("courseName");
        if (studentId == null) studentId = "";
        if (courseName == null) courseName = "";
        List dataList = getScoreMapList(studentId, courseName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/scoreDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Score s = null;
        Optional<Score> op;
        if (id != null) {
            op = scoreRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            scoreRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/scoreEditInit")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Score sc = null;
        Student s;
        Course c;
        Optional<Score> op;
        if (id != null) {
            op = scoreRepository.findById(id);
            if (op.isPresent()) {
                sc = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for (i = 0; i < sList.size(); i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("label", s.getStudentName());
            m.put("value", s.getId());
            studentIdList.add(m);
        }
        List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for (i = 0; i < cList.size(); i++) {
            c = cList.get(i);
            m = new HashMap();
            m.put("label", c.getCourseName());
            m.put("value", c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId", "");
        form.put("courseId", "");
        if (sc != null) {
            form.put("id", sc.getId());
            form.put("studentId", sc.getStudent().getId());
            form.put("courseId", sc.getCourse().getId());
            form.put("mark", sc.getMark());
        }
        form.put("studentIdList", studentIdList);
        form.put("courseIdList", courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewScoreId() {
        Integer id = scoreRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    @PostMapping("/scoreEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        Integer courseId = CommonMethod.getInteger(form, "courseId");
        Integer mark = CommonMethod.getInteger(form, "mark");
        Score sc = null;
        Student s = null;
        Course c = null;
        Optional<Score> op;
        if (id != null) {
            op = scoreRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                sc = op.get();
            }
        }
        if (sc == null) {
            sc = new Score();   //不存在 创建实体对象
            id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        Boolean isChosen=(scoreRepository.isCourseManagementExist(studentId,courseId).size()!=0);
        if(isChosen) {
        if (mark == null) return CommonMethod.getReturnMessageError("未输入成绩");
        sc.setMark(mark);
        double gradePoint = sc.getMark() >= 60 ? (sc.getMark() - 50) / 10.00 : 0;
        sc.setGradePoint(gradePoint);
        scoreRepository.save(sc);}  //新建和修改都调用save方法
        else return CommonMethod.getReturnMessageError("该学生未选择该科目");
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }

    public List getStudentMapListForQuary(String numName, String courseName, String scoreOrder) {
        List dataList = new ArrayList();
        //数据库查询操作
        List<Score> sList = scoreRepository.findByNumNameCourseName(numName, courseName);
//        System.out.println(sList);
        if (scoreOrder != null && scoreOrder != "") {
            if (scoreOrder.equals("分数降序")) {
                sList = scoreRepository.findByNumNameCourseNameScoreDescend(numName, courseName);
            } else if (scoreOrder.equals("分数升序")) {
                sList = scoreRepository.findByNumNameCourseNameScoreAscend(numName, courseName);
            } else if (scoreOrder.equals("低于60分")) {
                sList = scoreRepository.findByNumNameCourseNameScorefail(numName, courseName);
            }
        }
        if (sList == null || sList.size() == 0)
            return dataList;
        Score sc;
        Student s;
        Course c;
        Map m;
//        String courseParas,studentNameParas;
        for (int i = 0; i < sList.size(); i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("studentName", s.getStudentName());
            m.put("courseNum", c.getCourseNum());
            m.put("courseName", c.getCourseName());
            m.put("mark", sc.getMark());
            m.put("gradePoint", sc.getGradePoint());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/scoreQuery")
//    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        String courseName = dataRequest.getString("courseName");
        String scoreOrder = dataRequest.getString("scoreOrder");
        if (numName == null) numName = "";
        if (courseName == null) courseName = "";
        if (scoreOrder == null) scoreOrder = "";
        List dataList = getStudentMapListForQuary(numName, courseName, scoreOrder);
        return CommonMethod.getReturnData(dataList);
    }
}
