package hanium.englishfairytale.report.repository;

import hanium.englishfairytale.report.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
