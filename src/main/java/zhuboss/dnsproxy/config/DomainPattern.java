package zhuboss.dnsproxy.config;

import java.util.regex.Pattern;

public class DomainPattern {

    private Pattern regexPattern;

    private String fullTextMatch;

    private boolean useRegex;

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public void setRegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
    }

    public String getFullTextMatch() {
        return fullTextMatch;
    }

    public void setFullTextMatch(String fullTextMatch) {
        this.fullTextMatch = fullTextMatch;
    }

    public boolean isUseRegex() {
        return useRegex;
    }

    public void setUseRegex(boolean useRegex) {
        this.useRegex = useRegex;
    }

    /**
     * @param domain
     * @return
     */
    public static DomainPattern parse(String domain) {
        DomainPattern domainPattern = new DomainPattern();
        if (domain.contains("*")) {
            Pattern pattern = compileStringToPattern(domain);
            domainPattern.setRegexPattern(pattern);
            domainPattern.setUseRegex(true);
        } else {
            domainPattern.setFullTextMatch(domain+".");
            domainPattern.setUseRegex(false);
        }
        return domainPattern;
    }

    private static Pattern compileStringToPattern(String patternStr) {
        patternStr = "^" + patternStr;
        patternStr += ".";
        patternStr = patternStr.replace(".", "\\.");
        patternStr = patternStr.replace("*", ".*");
        patternStr += "$";
        return Pattern.compile(patternStr);
    }
}
