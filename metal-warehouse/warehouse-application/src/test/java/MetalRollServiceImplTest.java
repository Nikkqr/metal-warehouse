import com.warehouse.DTO.FilterDTO;
import com.warehouse.DTO.MetalRollDTO;
import com.warehouse.DTO.RollStatisticsDTO;
import com.warehouse.entities.MetalRoll;
import com.warehouse.exceptions.InvalidRollDataException;
import com.warehouse.exceptions.RollNotFoundException;
import com.warehouse.repositories.MetalRollRepository;
import com.warehouse.services.MetalRollServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MetalRollServiceImplTest {

    @Mock
    private MetalRollRepository repository;

    @InjectMocks
    private MetalRollServiceImpl service;

    @Test
    void shouldAddRoll() {
        when(repository.save(any(MetalRoll.class))).thenAnswer(inv -> {
            MetalRoll r = inv.getArgument(0);
            r.setId(12);
            return r;
        });

        MetalRollDTO result = service.addMetalRoll(10.0, 2.0);

        assertThat(result.getId()).isEqualTo(12);
        assertThat(result.getLength()).isEqualTo(10.0);
    }

    @Test
    void shouldThrowOnInvalidLength() {
        assertThatThrownBy(() -> service.addMetalRoll(-5.0, 2.0))
                .isInstanceOf(InvalidRollDataException.class)
                .hasMessage("Length and weight must be positive numbers.");
    }

    @Test
    void shouldRemoveRoll() {
        MetalRoll existing = new MetalRoll();
        existing.setId(42);
        existing.setLength(10.0);
        existing.setWeight(2.0);
        existing.setDateAdded(LocalDate.of(2026, 1, 1));
        existing.setDateOfDeletion(null);

        when(repository.findById(42)).thenReturn(Optional.of(existing));
        when(repository.save(any(MetalRoll.class))).thenAnswer(inv -> {
            MetalRoll r = inv.getArgument(0);
            return r;
        });

        MetalRollDTO result = service.removeMetalRoll(42);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42);
        assertThat(result.getDateOfDeletion()).isNotNull();
        verify(repository).findById(42);
        verify(repository).save(any(MetalRoll.class));
    }

    @Test
    void shouldThrowWhenRollNotFound() {
        when(repository.findById(777)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeMetalRoll(777))
                .isInstanceOf(RollNotFoundException.class)
                .hasMessage("MetalRoll with id 777 not found");
    }

    @Test
    void shouldFilterByLength() {
        MetalRoll r1 = new MetalRoll();
        r1.setId(1);
        r1.setLength(8.0);
        r1.setWeight(2.0);
        r1.setDateAdded(LocalDate.now());
        r1.setDateOfDeletion(null);

        MetalRoll r2 = new MetalRoll();
        r2.setId(2);
        r2.setLength(12.0);
        r2.setWeight(3.0);
        r2.setDateAdded(LocalDate.now());
        r2.setDateOfDeletion(null);

        when(repository.findAll()).thenReturn(List.of(r1, r2));

        FilterDTO filter = new FilterDTO();
        filter.setLengthFrom(7.0);
        filter.setLengthTo(10.0);

        List<MetalRollDTO> result = service.getFilteredMetalRolls(filter);

        assertThat(result.get(0).getId()).isEqualTo(1);
    }

    @Test
    void shouldCalculateStatistics() {
        MetalRoll r1 = new MetalRoll();
        r1.setId(1);
        r1.setLength(10.0);
        r1.setWeight(2.0);
        r1.setDateAdded(LocalDate.of(2026, 1, 5));
        r1.setDateOfDeletion(null);

        MetalRoll r2 = new MetalRoll();
        r2.setId(2);
        r2.setLength(15.0);
        r2.setWeight(3.0);
        r2.setDateAdded(LocalDate.of(2026, 1, 10));
        r2.setDateOfDeletion(LocalDate.of(2026, 1, 20));

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);

        when(repository.findActiveInPeriod(start, end)).thenReturn(List.of(r1, r2));
        when(repository.findByDateAddedBetween(start, end)).thenReturn(List.of(r1, r2));
        when(repository.findByDateOfDeletionBetween(start, end)).thenReturn(List.of(r2));

        RollStatisticsDTO stats = service.getStatistics(start, end);

        assertThat(stats.getAddedCount()).isEqualTo(2);
        assertThat(stats.getRemovedCount()).isEqualTo(1);
        assertThat(stats.getAvgLength()).isEqualTo(12.5);
        assertThat(stats.getMaxIntervalDays()).isEqualTo(10);
    }


}
