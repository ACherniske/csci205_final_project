/* *****************************************
 * CSCI 205 - Software Engineering and Design
 * Spring 2026
 *
 * Date: 4/28/2026
 * Time: 6:41 PM
 *
 * Project: csci205_final_project
 * Package: org.five_nights_at_dana.Rendering.Camera
 * Class: MockStudentManagerHelper
 *
 * Description:
 *
 * ****************************************
 */

package org.five_nights_at_dana.Rendering;

import org.five_nights_at_dana.AI.Location;
import org.five_nights_at_dana.AI.Student;
import org.five_nights_at_dana.Managers.StudentManager;

import java.util.ArrayList;
import java.util.List;

class MockStudentManagerHelper extends StudentManager {
    private final List<Student> studentList;
    public MockStudentManagerHelper(List<Student> list) { this.studentList = list; }

    @Override
    public List<Student> getStudentsAt(Location loc) {
        List<Student> found = new ArrayList<>();
        for(Student s : studentList) {
            if(s.getCurrentLocation() == loc) found.add(s);
        }
        return found;
    }
}
