package assistance_service.service;

import assistance_service.dto.AnnotationDTO;
import assistance_service.dto.AnnotationRequestDTO;
import assistance_service.entity.Annotation;
import assistance_service.repository.AnnotationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnnotationService Tests")
class AnnotationServiceTest {

    @Mock
    private AnnotationRepository annotationRepository;

    @InjectMocks
    private AnnotationService annotationService;

    private Annotation annotation;
    private AnnotationDTO annotationDTO;
    private AnnotationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        annotation = new Annotation();
        annotation.setId(1L);
        annotation.setStudentId(101L);
        annotation.setTeacherId(501L);
        annotation.setType("POSITIVE");
        annotation.setDescription("Great improvement");
        annotation.setActive(true);

        annotationDTO = AnnotationDTO.builder()
                .id(1L)
                .studentId(101L)
                .teacherId(501L)
                .type("POSITIVE")
                .description("Great improvement")
                .active(true)
                .build();

        requestDTO = new AnnotationRequestDTO();
        requestDTO.setStudentId(101L);
        requestDTO.setTeacherId(501L);
        requestDTO.setType("POSITIVE");
        requestDTO.setDescription("Great improvement");
    }

    @Test
    @DisplayName("Should find all annotations")
    void testFindAll() {
        Annotation annotation2 = new Annotation();
        annotation2.setId(2L);
        annotation2.setStudentId(102L);
        annotation2.setTeacherId(502L);
        annotation2.setType("NEGATIVE");
        annotation2.setDescription("Needs improvement");
        annotation2.setActive(true);

        when(annotationRepository.findAll()).thenReturn(Arrays.asList(annotation, annotation2));

        List<AnnotationDTO> result = annotationService.findAll();

        assertThat(result)
                .hasSize(2)
                .extracting(AnnotationDTO::getStudentId)
                .containsExactly(101L, 102L);

        verify(annotationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find annotations by student id")
    void testFindByStudentId() {
        when(annotationRepository.findByStudentId(101L)).thenReturn(List.of(annotation));

        List<AnnotationDTO> result = annotationService.findByStudentId(101L);

        assertThat(result)
                .hasSize(1)
                .extracting(AnnotationDTO::getType)
                .containsExactly("POSITIVE");

        verify(annotationRepository, times(1)).findByStudentId(101L);
    }

    @Test
    @DisplayName("Should find annotations by student id and type")
    void testFindByStudentIdAndType() {
        when(annotationRepository.findByStudentIdAndType(101L, "POSITIVE")).thenReturn(List.of(annotation));

        List<AnnotationDTO> result = annotationService.findByStudentIdAndType(101L, "POSITIVE");

        assertThat(result)
                .hasSize(1)
                .extracting(AnnotationDTO::getDescription)
                .containsExactly("Great improvement");

        verify(annotationRepository, times(1)).findByStudentIdAndType(101L, "POSITIVE");
    }

    @Test
    @DisplayName("Should create annotation successfully")
    void testCreate() {
        when(annotationRepository.save(any(Annotation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnnotationDTO result = annotationService.create(requestDTO);

        assertThat(result)
                .isNotNull()
                .extracting(AnnotationDTO::getStudentId, AnnotationDTO::getType, AnnotationDTO::getActive)
                .containsExactly(101L, "POSITIVE", true);

        verify(annotationRepository, times(1)).save(any(Annotation.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when annotation type is invalid")
    void testCreateInvalidType() {
        requestDTO.setType("NEUTRAL");

        assertThatThrownBy(() -> annotationService.create(requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid annotation type. Use 'POSITIVE' or 'NEGATIVE'");

        verify(annotationRepository, never()).save(any(Annotation.class));
    }

    @Test
    @DisplayName("Should delete annotation by setting active to false")
    void testDelete() {
        Annotation inactiveAnnotation = new Annotation();
        inactiveAnnotation.setId(1L);
        inactiveAnnotation.setStudentId(101L);
        inactiveAnnotation.setTeacherId(501L);
        inactiveAnnotation.setType("POSITIVE");
        inactiveAnnotation.setDescription("Great improvement");
        inactiveAnnotation.setActive(false);

        when(annotationRepository.findById(1L)).thenReturn(Optional.of(annotation));
        when(annotationRepository.save(any(Annotation.class))).thenReturn(inactiveAnnotation);

        annotationService.delete(1L);

        verify(annotationRepository, times(1)).findById(1L);
        verify(annotationRepository, times(1)).save(any(Annotation.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when annotation not found")
    void testDeleteNotFound() {
        when(annotationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> annotationService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Annotation not found");

        verify(annotationRepository, times(1)).findById(999L);
        verify(annotationRepository, never()).save(any(Annotation.class));
    }
}