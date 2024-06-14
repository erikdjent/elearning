package com.imsoftware.students.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.imsoftware.students.repository.StudentRepository;
import org.springframework.stereotype.Service;

import com.imsoftware.students.domain.StudentDTO;
import com.imsoftware.students.entity.Student;
import com.imsoftware.students.entity.Subject;
import com.imsoftware.students.service.IStudentService;

@Service
public class StudentServiceImpl implements IStudentService {

	private final StudentRepository studentRepository;

	public StudentServiceImpl(StudentRepository studentRepository) {
		super();
		this.studentRepository = studentRepository;
	}

	@Override
	public Collection<StudentDTO> findAll() {
		return studentRepository.findAll().stream().map(new Function<Student, StudentDTO>() {
			@Override
			public StudentDTO apply(Student student) {
				List<String> programmingLanguagesKnowAbout = student.getSubjects().stream()
						.map(pl -> new String(pl.getName())).collect(Collectors.toList());
				return new StudentDTO(student.getName(), programmingLanguagesKnowAbout);
			}

		}).collect(Collectors.toList());

	}

	@Override
	public Collection<StudentDTO> findAllAndShowIfHaveAPopularSubject() {
		// TODO Obtener la lista de todos los estudiantes e indicar la materia más
		// concurrida existentes en la BD e
		// indicar si el estudiante cursa o no la materia más concurrida registrado en
		// la BD.
		List<String> subjects = new ArrayList<>();
		studentRepository.findAll().forEach(v -> {
			v.getSubjects().forEach(k -> {
				subjects.add(k.getName());
			});

		});

		Map<String, Long> frequencyMap = new HashMap<>();
		for (String s : subjects) {
			frequencyMap.merge(s, 1L, Long::sum);
		}

		Map.Entry<String, Long> currentSubject = frequencyMap.entrySet().stream()
				.max(Comparator.comparing(Map.Entry::getValue)).orElse(null);

		return studentRepository.findAll().stream().map(new Function<Student, StudentDTO>() {
			@Override
			public StudentDTO apply(Student student) {
				List<String> programmingLanguagesKnowAbout = student.getSubjects().stream()
						.map(pl -> new String(pl.getName())).collect(Collectors.toList());
				return new StudentDTO(student.getName(), programmingLanguagesKnowAbout.contains(currentSubject.getKey()),
						programmingLanguagesKnowAbout);
			}

		}).collect(Collectors.toList());

	}

}
