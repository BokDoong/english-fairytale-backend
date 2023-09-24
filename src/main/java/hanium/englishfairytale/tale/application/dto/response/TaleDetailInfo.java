package hanium.englishfairytale.tale.application.dto.response;

import hanium.englishfairytale.tale.domain.Image;
import hanium.englishfairytale.tale.domain.Keyword;
import hanium.englishfairytale.tale.domain.Tale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaleDetailInfo {

    private String title;
    private String content;
    private String kor;
    private List<String> keywords;
    private String imgUrl;

    public TaleDetailInfo(Tale tale, List<Keyword> keywords) {
        this.title = tale.getTitle();
        this.content = tale.getEngTale();
        this.kor = tale.getKorTale();
        this.imgUrl = tale.getImage().getTaleImage().getImageUrl();

        this.keywords = new ArrayList<>();
        for(Keyword keyword:keywords) {
            this.keywords.add(keyword.getWord());
        }
    }

}
