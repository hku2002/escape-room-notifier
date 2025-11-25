package kr.co.escape.api.dto.response;

import kr.co.escape.api.entity.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ThemeResponse {

    private Long themeId;
    private String themeName;
    private String cafeName;
    private String branchName;

    public static ThemeResponse from(Theme theme) {
        return new ThemeResponse(
                theme.getId(),
                theme.getThemeName(),
                theme.getCafeName(),
                theme.getBranch()
        );
    }
}