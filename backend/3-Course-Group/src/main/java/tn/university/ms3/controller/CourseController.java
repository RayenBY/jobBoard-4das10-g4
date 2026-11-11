package tn.university.ms3.controller;
import tn.university.ms3.entity.Course;
import tn.university.ms3.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/courses") @CrossOrigin
public class CourseController {
 private final CourseRepository repository;
 public CourseController(CourseRepository repository){this.repository=repository;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)
 public Course create(@RequestBody Course course){course.setId(null);return repository.save(course);}
 @GetMapping public List<Course> findAll(){return repository.findAll();}
 @GetMapping("/{id}") public Course findById(@PathVariable Long id){return repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found"));}
 @PutMapping("/{id}") public Course update(@PathVariable Long id,@RequestBody Course input){
  Course c=repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found"));
  c.setTitle(input.getTitle());c.setDescription(input.getDescription());c.setSubject(input.getSubject());c.setLevel(input.getLevel());return repository.save(c);
 }
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
 public void delete(@PathVariable Long id){if(!repository.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found");repository.deleteById(id);}
}
