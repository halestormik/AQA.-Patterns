import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ReplanCardDeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @BeforeAll
     static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    public void shouldTestFormWithAnotherDateOfDelivery() { // проверка Happy path
        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(DataGenerator.generateCity("ru"));
        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        form.$("[data-test-id=name] input").setValue(DataGenerator.generateName("ru"));
        form.$("[data-test-id=phone] input").setValue(DataGenerator.generatePhone("ru"));
        form.$("[data-test-id=agreement]").click();
        form.$$("button").last().click();

        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + DataGenerator.generateDate(4)))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(6)); // установка другой даты
        form.$$("button").last().click();
        $("[data-test-id=replan-notification]")
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                .shouldBe(Condition.visible);

        $("[data-test-id=replan-notification] .button").click(); // клик по кнопке "Перепланировать"

        $("[data-test-id=success-notification]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + DataGenerator.generateDate(6)))
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldTestFormWithIncorrectCityName() { // проверка валидации поля "город"
        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(DataGenerator.generateCity("en"));
        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        form.$("[data-test-id=name] input").setValue(DataGenerator.generateName("ru"));
        form.$("[data-test-id=phone] input").setValue(DataGenerator.generatePhone("ru"));
        form.$("[data-test-id=agreement]").click();
        form.$$("button").last().click();

        $("[data-test-id=city].input_invalid")
                .shouldHave(Condition.text("Доставка в выбранный город недоступна"))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=city] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля "Фамилия и имя"
        form.$("[data-test-id=city] input").setValue(""); // ввод пустой строки
        form.$$("button").last().click();
        $("[data-test-id=city].input_invalid")
                .shouldHave(Condition.text("Поле обязательно для заполнения"))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=city] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля "Фамилия и имя"
        form.$("[data-test-id=city] input").setValue(" "); // ввод пробела
        form.$$("button").last().click();
        $("[data-test-id=city].input_invalid")
                .shouldHave(Condition.text("Поле обязательно для заполнения"))
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldTestFormWithIncorrectDate() { // проверка валидации поля "дата"
        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(DataGenerator.generateCity("ru"));
        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(0)); // вставка текущей даты
        form.$("[data-test-id=name] input").setValue(DataGenerator.generateName("ru"));
        form.$("[data-test-id=phone] input").setValue(DataGenerator.generatePhone("ru"));
        form.$("[data-test-id=agreement]").click();
        form.$$("button").last().click();

        $("[data-test-id=date] .input_invalid")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(-1)); // вставка даты, предшествующей текущей
        form.$$("button").last().click();
        $("[data-test-id=date] .input_invalid")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(1)); // вставка даты, следующей за текущей
        form.$$("button").last().click();
        $("[data-test-id=date] .input_invalid")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(2)); // вставка даты +2 дня за текущей
        form.$$("button").last().click();
        $("[data-test-id=date] .input_invalid")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"))
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldTestFormWithIncorrectName() { // проверка валидации поля "имя"
        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(DataGenerator.generateCity("ru"));
        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        form.$("[data-test-id=name] input").setValue(DataGenerator.generateName("en")); // генерация имени с локалью en
        form.$("[data-test-id=phone] input").setValue(DataGenerator.generatePhone("ru"));
        form.$("[data-test-id=agreement]").click();
        form.$$("button").last().click();

        $("[data-test-id=name].input_invalid")
                .shouldHave(Condition.text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=name] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля "Фамилия и имя"
        form.$("[data-test-id=name] input").setValue(""); // ввод пустой строки
        form.$$("button").last().click();
        $("[data-test-id=name].input_invalid")
                .shouldHave(Condition.text("Поле обязательно для заполнения"))
                .shouldBe(Condition.visible);

        form.$("[data-test-id=name] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля "Фамилия и имя"
        form.$("[data-test-id=name] input").setValue(" "); // ввод пробела
        form.$$("button").last().click();
        $("[data-test-id=name].input_invalid")
                .shouldHave(Condition.text("Поле обязательно для заполнения"))
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldTestFormWithIncorrectPhone() { // проверка валидации поля "телефона"
        SelenideElement form = $("form");
        form.$("[data-test-id=city] input").setValue(DataGenerator.generateCity("ru"));
        form.$("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.BACK_SPACE); // очистка поля даты
        form.$("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        form.$("[data-test-id=name] input").setValue(DataGenerator.generateName("ru"));
        form.$("[data-test-id=phone] input").setValue("+9514"); // ввод заведомо неверного номера телефона (меньше 11 цифр)
        form.$("[data-test-id=agreement]").click();
        form.$$("button").last().click();

        $("[data-test-id=phone].input_invalid")
                .shouldHave(Condition.text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."))
                .shouldBe(Condition.visible);
    }
}
