CREATE OR REPLACE PACKAGE proj2 AS
TYPE reference_cursor is ref cursor;

  PROCEDURE show_students(cur OUT reference_cursor );
  PROCEDURE show_courses(cur OUT reference_cursor );
  PROCEDURE show_prerequisites(cur OUT reference_cursor );
  PROCEDURE show_course_credit(cur OUT reference_cursor );
  PROCEDURE show_classes(cur OUT reference_cursor );
  PROCEDURE show_score_grade(cur OUT reference_cursor );
  PROCEDURE show_g_enrollments(cur OUT reference_cursor );


  PROCEDURE prerequisite_courses(DEPT IN PREREQUISITES.DEPT_CODE%TYPE, COURSEID IN PREREQUISITES."COURSE#"%TYPE, cur OUT SYS_REFCURSOR, error_info OUT VARCHAR);
  PROCEDURE delete_student(P_B# IN students."B#"%TYPE, B_ID IN g_enrollments."G_B#"%TYPE, error_info OUT VARCHAR2);


  PROCEDURE DropGraduateStudent(p_classid IN g_enrollments.CLASSID%TYPE, p_B# IN g_enrollments."G_B#"%TYPE, error_info out varchar); 

  PROCEDURE show_logs(cur OUT reference_cursor );
  PROCEDURE list_students(cur OUT reference_cursor, classid_in IN classes.CLASSID%TYPE,  error_info  OUT VARCHAR);

  PROCEDURE EnrollGraduateStudent(p_classid IN g_enrollments.CLASSID%TYPE, p_B# IN g_enrollments."G_B#"%TYPE, error_info  OUT VARCHAR);



END proj2;
/

CREATE OR REPLACE PACKAGE BODY proj2 AS

  PROCEDURE show_students(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from students;
  END ;


  PROCEDURE show_courses(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from courses;
  END ;


  PROCEDURE show_prerequisites(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from prerequisites;
  END ;


  PROCEDURE show_course_credit(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from course_credit;
  END ;


  PROCEDURE show_classes(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from classes;
  END ;


  PROCEDURE show_score_grade(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from score_grade;
  END ;


  PROCEDURE show_g_enrollments(cur OUT reference_cursor) is
  BEGIN
  OPEN cur for select * from g_enrollments;
  END ;


  /*option 4 Delete student from the student table */

  PROCEDURE delete_student(P_B# IN students."B#"%TYPE, B_ID IN g_enrollments."G_B#"%TYPE, error_info OUT VARCHAR2) IS
      v_count NUMBER;
  BEGIN
      SELECT COUNT(*) INTO v_count FROM students s WHERE s."B#" = P_B#;
      
      IF v_count != 0 THEN
          DELETE FROM g_enrollments g_e WHERE g_e."G_B#" = B_ID;
          DELETE FROM students s WHERE s."B#" = P_B#;
          error_info := 'Successfully removed student ' || P_B#;
      ELSE
          error_info := 'The B# is invalid';
      END IF;
  END delete_student;



/*option 2:  Enroll grad student into a class */


PROCEDURE EnrollGraduateStudent(p_classid IN g_enrollments.CLASSID%TYPE, p_B# IN g_enrollments."G_B#"%TYPE, error_info  OUT VARCHAR) IS     
        
        v_count_cl NUMBER;
        v_count_in NUMBER;
        v_count_c_s NUMBER;
        v_count_s_st NUMBER;
        v_count_f NUMBER;
        v_count NUMBER;
        v_count_c NUMBER;
        v_count_b NUMBER;
    
    BEGIN SELECT COUNT(*) INTO v_count_c FROM classes cl WHERE cl.CLASSID = p_classid;
          SELECT COUNT(*) INTO v_count_b FROM students st WHERE st."B#" = p_B#;
          SELECT COUNT(*) INTO v_count_s_st FROM students st WHERE st."B#" = p_B# AND (st.ST_LEVEL = 'PhD' OR st.ST_LEVEL = 'master');

          
          SELECT COUNT(*) INTO v_count_f FROM g_enrollments g_e, classes cl WHERE g_e.CLASSID = cl.CLASSID AND g_e."G_B#" = p_B# AND cl."YEAR" = 2021 AND cl.SEMESTER = 'Spring';

          SELECT LIMIT-class_size INTO v_count_cl FROM classes WHERE classid =p_classid;
          SELECT COUNT(*) INTO v_count_in FROM g_enrollments g_e WHERE g_e.CLASSID = p_classid AND g_e."G_B#" = p_B#;
          
          SELECT COUNT(*) INTO v_count FROM classes cl, prerequisites p WHERE cl.CLASSID = p_classid AND cl."COURSE#" = p."COURSE#" AND p."PRE_COURSE#" NOT IN 
                  (SELECT cl."COURSE#" FROM g_enrollments g_e, classes cl, score_grade s_g WHERE g_e."G_B#" = p_B# AND g_e.CLASSID = cl.CLASSID  AND s_g.LGRADE IN ('A', 'A-', 'B+', 'B', 'B-', 'C+', 'C') AND g_e.SCORE = s_g.SCORE);

          SELECT COUNT(*) INTO v_count_c_s FROM classes WHERE classid = p_classid AND semester = 'Spring' AND year = 2021;

      
       if v_count_b = 0 THEN
            error_info := 'The B# is invalid.';
      
      ELSIF v_count_s_st = 0 THEN
            error_info := 'This is not a graduate student.';

      ELSIF v_count != 0 THEN
            error_info := 'Prerequisite not satisfied.';
      
      ELSIF v_count_c = 0  THEN
            error_info := 'The classid is invalid.';
   
      ELSIF v_count_c_s = 0 THEN
            error_info := 'Cannot enroll into a class from a previous semester.';
  
      ELSIF v_count_cl = 0 THEN
           error_info := 'The class is already full.';
     
      ELSIF v_count_in != 0 THEN
           error_info := 'The student is already in the class.';
      
      
      ELSIF v_count_c = 0  THEN
            error_info := 'The classid is invalid.';
     
      ELSIF v_count_f > 4 THEN
           error_info := 'Students cannot be enrolled in more than five classes in the same semester.';

      ELSE
         INSERT INTO g_enrollments VALUES (p_B#, p_classid, NULL);
        error_info := 'Student successfully enrolled in a graduate class.';
        
      end if;

end EnrollGraduateStudent;

/*
option 5 Display Every Student in a class

 */

PROCEDURE list_students(cur OUT reference_cursor, classid_in IN classes.CLASSID%TYPE ,error_info  OUT VARCHAR) IS
        class_number NUMBER;
    
    BEGIN SELECT count(*) INTO class_number FROM classes cl WHERE cl.CLASSID=classid_in;
        
        IF class_number != 0 
        THEN
          OPEN cur FOR 
          
          SELECT st."B#", st.first_name, st.last_name FROM g_enrollments g_e, students st WHERE g_e.CLASSID = classid_in AND g_e."G_B#" = st."B#";
          
        ELSE
          OPEN cur FOR 
          
          SELECT st."B#", st.first_name, st.last_name FROM g_enrollments g_e, students st WHERE g_e.CLASSID = classid_in AND g_e."G_B#" = st."B#";
          error_info := 'The classid is invalid.'; 

        END IF;  

    END list_students;







/* 3. DELETE a Graduate student from a class */ 

PROCEDURE DropGraduateStudent(p_classid IN g_enrollments.CLASSID%TYPE, p_B# IN g_enrollments."G_B#"%TYPE, error_info out varchar) IS
        -- local variables

        
        gst_value NUMBER;
        v_count_c NUMBER;
        class_number NUMBER;
        v_is_last_class NUMBER;

        v_is_current_semester NUMBER;
        st_value NUMBER;

      /* Conditions Checks */
          --condition check 1
    BEGIN SELECT COUNT(*) INTO gst_value FROM students st WHERE (st.ST_LEVEL = 'PhD' OR st.ST_LEVEL = 'master') AND st."B#" = p_B#;
          
          --condition check 2
          SELECT COUNT(*) INTO class_number FROM g_enrollments g_e WHERE g_e."G_B#" = p_B# AND g_e.CLASSID = p_classid;
          
          --condition check 3
          SELECT COUNT(*) INTO v_is_last_class FROM g_enrollments g_e, classes cl WHERE cl."YEAR" = 2021 AND cl.SEMESTER = 'Spring' AND g_e."G_B#" = p_B# AND g_e.CLASSID = cl.CLASSID;

          --condition check 4
          SELECT COUNT(*) INTO v_is_current_semester FROM classes  WHERE  "YEAR" = 2021 AND CLASSID=p_classid AND semester = 'Spring';
          
          --condition check 5
          SELECT COUNT(*) INTO v_count_c FROM classes cl WHERE cl.CLASSID = p_classid;

        IF st_value = 0 THEN
              error_info := 'The B# is invalid.';
        
        ELSIF gst_value = 0 THEN
             error_info := 'This is not a graduate student.';
       
        ELSIF v_is_current_semester = 0 THEN
             error_info := 'Only enrollment in the current semester can be dropped.';
        
        ELSIF v_count_c = 0 THEN
             error_info := 'The classid is invalid.';
        
        ELSIF v_is_last_class = 1 THEN
             error_info := 'This is the only class for this student in spring 2021 and cannot be dropped.';
        
        
        ELSIF class_number = 0 THEN
              error_info :='The student is not enrolled in the class.';
        
        -- Every condition checked, so deleting the student 
        ELSE DELETE FROM g_enrollments g_e WHERE  g_e.CLASSID=p_classid AND g_e."G_B#"=p_B#;
             error_info := 'B# '||p_B#||' is  dropped from the class: '|| p_classid;

        end if; 
    end DropGraduateStudent;


  /*option 6: Show prerequisite courses */

  PROCEDURE prerequisite_courses(DEPT IN PREREQUISITES.DEPT_CODE%TYPE, COURSEID IN PREREQUISITES."COURSE#"%TYPE, cur OUT SYS_REFCURSOR, error_info OUT VARCHAR) IS
      course_count NUMBER;
  BEGIN
      SELECT COUNT(*) INTO course_count FROM COURSES WHERE DEPT_CODE = DEPT AND "COURSE#" = COURSEID;
        
      IF course_count = 0 THEN
          error_info := 'The ' || DEPT || COURSEID || ' does not exist.';
      ELSE 
          OPEN cur FOR
              SELECT q.preq FROM (
                  SELECT CONNECT_BY_ROOT ac.course c, ac.preq preq 
                  FROM (
                      SELECT p.DEPT_CODE || p."COURSE#" course, p.PRE_DEPT_CODE || p."PRE_COURSE#" preq 
                      FROM PREREQUISITES p
                  ) ac
                  CONNECT BY PRIOR ac.preq = ac.course
              ) q
              WHERE q.c = DEPT || COURSEID;
      END IF;   
  END prerequisite_courses;

PROCEDURE show_logs(cur OUT reference_cursor) is
BEGIN
OPEN cur for select * from logs;
END ;

END;
/
show errors


/* Triggers */

CREATE OR REPLACE TRIGGER enrollment_trigger_insert
AFTER INSERT ON G_ENROLLMENTS
FOR EACH ROW
BEGIN
    INSERT INTO logs(log#, user_name, op_time, table_name, operation, tuple_keyvalue)
  VALUES(log_seq.NEXTVAL, USER, SYSDATE, 'g_enrollments', 'INSERT', :NEW.G_B# || ',' || :NEW.classid);  
END;
/

CREATE OR REPLACE TRIGGER student_delete_trigger
AFTER DELETE ON students
FOR EACH ROW
BEGIN
  INSERT INTO logs(log#, user_name, op_time, table_name, operation, tuple_keyvalue)
  VALUES(log_seq.NEXTVAL, USER, SYSDATE, 'students', 'DELETE', :OLD.b#);
END;
/

CREATE OR REPLACE TRIGGER enrollment_trigger_delete
AFTER DELETE ON g_enrollments
FOR EACH ROW
BEGIN
    INSERT INTO logs(log#, user_name, op_time, table_name, operation, tuple_keyvalue)
  VALUES(log_seq.NEXTVAL, USER, SYSDATE, 'g_enrollments', 'DELETE', :OLD.G_B# || ',' || :OLD.classid);  
END;
/

show ERRORS