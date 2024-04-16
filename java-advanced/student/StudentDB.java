package info.kgeorgiy.ja.khodzhayarov.student;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

public class StudentDB implements StudentQuery {

    public static final Comparator<Student> STUDENT_COMPARATOR = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .reversed()
            .thenComparingInt(Student::getId);

    public static final Comparator<Student> ID_COMPARATOR = Comparator.comparingInt(Student::getId);

    public static final Collector<Student, ?, Map<String, String>> NAME_BY_GROUP_COLLECTOR =
            Collectors.toMap(
                    Student::getLastName,
                    Student::getFirstName,
                    BinaryOperator.minBy(String::compareTo)
            );

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        return map(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        return map(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(final List<Student> students) {
        return map(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        return map(students, student ->
                String.join(" ", student.getFirstName(), student.getLastName())
        );
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return mapAndCollect(
                students,
                Student::getFirstName,
                Collectors.toCollection(TreeSet::new)
        );
    }

    private <E> List<E> map(final List<Student> students, final Function<Student, E> function) {
        return mapAndCollect(students, function, Collectors.toList());
    }

    private <E, C> C mapAndCollect(final List<Student> students, final Function<Student, E> function, final Collector<E, ?, C> collector) {
        return students.stream().map(function).collect(collector);
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sort(students, ID_COMPARATOR);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sort(students, STUDENT_COMPARATOR);
    }

    private List<Student> sort(Collection<Student> students, Comparator<Student> nameComparator) {
        return students.stream().sorted(nameComparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return find(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return find(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return find(students, Student::getGroup, group);
    }

    private <E> List<Student> find(Collection<Student> students, Function<Student, E> function, E toCompare) {
        return filter(students, function, toCompare)
                .sorted(STUDENT_COMPARATOR)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return filter(students, Student::getGroup, group)
                .collect(NAME_BY_GROUP_COLLECTOR);
    }

    private <E> Stream<Student> filter(Collection<Student> students, Function<Student, E> function, E toCompare) {
        return students.stream()
                .filter(student -> function.apply(student).equals(toCompare));
    }

}
