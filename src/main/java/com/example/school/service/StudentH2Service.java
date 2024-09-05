package com.example.school.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;

import com.example.school.model.Student;
import com.example.school.model.StudentRowMapper;
import com.example.school.repository.StudentRepository;

@Service
public class StudentH2Service implements StudentRepository {

	@Autowired
	private JdbcTemplate db;

	@Override
	public ArrayList<Student> getStudents() {
		List<Student> studentsList = db.query("SELECT * FROM STUDENT", new StudentRowMapper());
		ArrayList<Student> students = new ArrayList<>(studentsList);
		return students;
	}

	@Override
	public Student getStudentById(int studentId) {
		try {
			Student student = db.queryForObject("SELECT * FROM STUDENT WHERE studentId = ?",
					new StudentRowMapper(), studentId);
			return student;
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public Student addStudent(Student student) {
		db.update("INSERT INTO STUDENT(studentName, gender,standard) values (?, ?, ?)",
				student.getStudentName(), student.getGender(), student.getStandard());

		Student savedStudent = db.queryForObject(
				"SELECT * FROM STUDENT WHERE studentName = ? and gender = ? and standard = ?",
				new StudentRowMapper(), student.getStudentName(), student.getGender(), student.getStandard());
		return savedStudent;
	}

	@Override
	public String addMultipleStudents(ArrayList<Student> studentsList) {
		for (Student eachStudent : studentsList) {
			db.update("INSERT INTO STUDENT (studentName, gender,standard) VALUES (?, ?, ?)",
					eachStudent.getStudentName(), eachStudent.getGender(), eachStudent.getStandard());
		}
		String message = String.format("Successfully added %d students", studentsList.size());
		return message;
	}

	@Override
	public Student updateStudent(int studentId, Student student) {
		if (student.getStudentName() != null) {
			db.update("UPDATE STUDENT set studentName = ? where studentId = ?",
					student.getStudentName(), studentId);
		}
		if (student.getGender() != null) {
			db.update("UPDATE STUDENT set gender = ? where studentId = ?",
					student.getGender(), studentId);
		}
		if (student.getStandard() != 0) {
			db.update("UPDATE STUDENT set standard = ? where studentId = ?", student.getStandard(), studentId);
		}

		return getStudentById(studentId);
	}

	@Override
	public void deleteStudent(int studentId) {
		db.update("DELETE FROM STUDENT WHERE studentId = ?", studentId);
	}

}