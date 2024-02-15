
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }
    private static Faker faker;

    public static String PhonePattern(String phone) {  // убираем лишние символы из строки телефона
        return phone.replace("(","")
                .replace(")","")
                .replace("-", "");
    }

    public static String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity(String  locale) {
        faker = new Faker(new Locale(locale));
        return faker.address().city();
    }

    public static String generateName(String  locale) {
        faker = new Faker(new Locale(locale));
       return faker.name().fullName();
    }

    public static String generatePhone(String  locale) {
        faker = new Faker(new Locale(locale));
        return PhonePattern(faker.phoneNumber().phoneNumber());
    }
}

