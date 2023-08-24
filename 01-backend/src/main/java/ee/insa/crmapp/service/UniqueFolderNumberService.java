package ee.insa.crmapp.service;

import ee.insa.crmapp.model.UniqueFolderNumber;
import ee.insa.crmapp.repository.UniqueFolderNumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class UniqueFolderNumberService {
    private final UniqueFolderNumberRepository uniqueFolderNumberRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");

    @Autowired
    public UniqueFolderNumberService(UniqueFolderNumberRepository uniqueFolderNumberRepository) {
        this.uniqueFolderNumberRepository = uniqueFolderNumberRepository;
    }

    public String getFolderNumber() {
        UniqueFolderNumber uniqueFolderNumber = uniqueFolderNumberRepository.findById(1L).orElse(null);

        if (uniqueFolderNumber == null) {
            uniqueFolderNumber = new UniqueFolderNumber(1L, LocalDate.now(), 0);
        }

        if (uniqueFolderNumber.getDate().equals(LocalDate.now())) {
            // если последняя запись от сегодня, то увеличиваем на 1 и возвращаем строку
            int lastNumber = uniqueFolderNumber.getLastUsedNumber() + 1;
            uniqueFolderNumber.setLastUsedNumber(lastNumber);
        } else {
            // если последняя запись не сегодня - создаем первую сегодняшнюю запись и возвращаем строку
            uniqueFolderNumber.setDate(LocalDate.now());
            uniqueFolderNumber.setLastUsedNumber(1);
        }

        uniqueFolderNumberRepository.save(uniqueFolderNumber);
        return uniqueFolderNumber.getDate().format(DATE_FORMATTER) + String.format("_%03d", uniqueFolderNumber.getLastUsedNumber());
    }
}
