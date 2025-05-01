package hekireki.sanjijiksong.domain.openapi.service;

import hekireki.sanjijiksong.domain.openapi.Repository.PriceDailySearchRepository;
import hekireki.sanjijiksong.domain.openapi.Repository.TrendingKeywordRepository;
import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import hekireki.sanjijiksong.domain.openapi.dto.TrendingKeywordPrice;
import hekireki.sanjijiksong.domain.openapi.service.webdriver.WebDriverProvider;
import hekireki.sanjijiksong.domain.openapi.entity.TrendingKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendingKeywordService {
    private final TrendingKeywordRepository trendingKeywordRepository;
    private final PriceDailySearchRepository priceDailySearchRepository;
    private final WebDriverProvider webDriverProvider;

    /**
     * 오늘 날짜의 TrendingKeyword 각각에 대해, PriceDaily 테이블에서 itemName에 해당 키워드가 포함된 최신 레코드를 조회하여
     * TrendingKeywordPrice로 반환합니다.
     * <p>
     * 반환 타입은 Map<String, TrendingKeywordPriceDto>로, 키는 인기 검색어, 값은 해당 DTO입니다.
     */
    @Transactional(readOnly = true)
    public Map<String, TrendingKeywordPrice> getTrendingKeywordsPriceInfo() {
        List<TrendingKeyword> trendingKeywords = trendingKeywordRepository.findByCreateDate(LocalDate.now());

        if (trendingKeywords.isEmpty()) {
            log.info("오늘 날짜에 해당하는 인기 검색어가 없습니다.");
            return Collections.emptyMap();
        }
        Map<String, TrendingKeywordPrice> result = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 1); // 최신 1개만 조회

        for (TrendingKeyword tk : trendingKeywords) {
            List<PriceDailyDocument> docs = priceDailySearchRepository
                    .findTopByItemNameContainingOrderBySnapshotDateDesc(tk.getKeyword(), pageable);

            if (!docs.isEmpty()) {
                PriceDailyDocument doc = docs.get(0);

                TrendingKeywordPrice dto = new TrendingKeywordPrice(
                        tk.getKeyword(),
                        tk.getCategory(),
                        tk.getRank(),
                        tk.getCreateDate(),
                        doc.getItemName(),
                        doc.getPrice(),
                        doc.getSnapshotDate()
                );
                result.put(tk.getKeyword(), dto);
                log.info("키워드 [{}]에 대한 최신 가격 정보 DTO 생성: {}", tk.getKeyword(), dto);
            } else {
                log.info("키워드 [{}]에 매칭되는 가격 정보가 없습니다.", tk.getKeyword());
            }
        }

        return Collections.unmodifiableMap(result);
    }

    public void saveTodayTrendingKeywords() {
        WebDriver driver = webDriverProvider.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            log.info("크롤링 시작");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            driver.get("https://datalab.naver.com/shoppingInsight/sCategory.naver");

            waitUntilPageLoad(wait);

            selectTopCategory(wait, "식품");

            // 순서 보장을 위해 LinkedHashMap 사용
            Map<Integer, String> subCategories = new LinkedHashMap<>();
            subCategories.put(1, "축산물");
            subCategories.put(2, "수산물");
            subCategories.put(3, "농산물");

            for (Map.Entry<Integer, String> entry : subCategories.entrySet()) {
                int liIndex = entry.getKey();
                String subCategory = entry.getValue();

                selectSubCategory(wait, liIndex);
                clickSearchButton(wait);

                List<String> keywords = extractTopKeywords(driver, 10);
                log.info("[{}] 인기 검색어: {}", subCategory, keywords);

                saveTrendingKeywords(keywords, subCategory);
                log.info("[{}] 인기 검색어 저장 완료", subCategory);
            }

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생", e);
        } finally {
            driver.quit();
        }
    }

    private void waitUntilPageLoad(WebDriverWait wait) {
        wait.until(driver -> Objects.equals(((JavascriptExecutor) driver)
                .executeScript("return document.readyState"), "complete"));
    }

    private void selectTopCategory(WebDriverWait wait, String categoryName) {
        WebElement categoryButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("div.set_period.category > div:nth-child(1) > span")));
        categoryButton.click();

        WebElement foodOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("div.set_period.category > div:nth-child(1) > ul > li:nth-child(7) > a")
            ));
        foodOption.click();
        log.info("상위 카테고리 '{}' 선택 완료", categoryName);
    }

    private void selectSubCategory(WebDriverWait wait, int liIndex) {
        WebElement subCategoryButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div.set_period.category > div:nth-child(2) > span")));
        subCategoryButton.click();

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='content']/div[2]/div/div[1]/div/div/div[1]/div/div[2]/ul/li[" + liIndex + "]")));
        option.click();
        log.info("2분류 카테고리 옵션 {} 선택 완료", liIndex);
    }

    private void clickSearchButton(WebDriverWait wait) throws InterruptedException {
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div.section.insite_inquiry > div > a")));
        searchButton.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.loading") // 로딩 요소
        ));
        log.info("조회 버튼 클릭 완료");
    }

    private List<String> extractTopKeywords(WebDriver driver, int limit) {
        List<WebElement> keywordElements = driver.findElements(By.cssSelector("li a.link_text"));
        return keywordElements.stream()
                .map(e -> e.getText().replaceAll("^\\d+\\s*", "").trim())
                .filter(s -> !s.isBlank())
                .limit(limit)
                .toList();
    }

    private void saveTrendingKeywords(List<String> keywords, String category) {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < keywords.size(); i++) {
            TrendingKeyword keywordEntity = TrendingKeyword.builder()
                    .keyword(keywords.get(i))
                    .rank(i + 1)
                    .category(category)
                    .createDate(today)
                    .build();
            trendingKeywordRepository.save(keywordEntity);
        }
    }


}
