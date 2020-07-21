use(function()
{
    var page = {};

    function isErrorPage() {
        var isError = false;
        var templatePath = pageProperties.get("cq:template", "");
        if (templatePath === "/apps/ezpizee/templates/errorpage") {
            isError = true;
        }
        return isError;
    }

    function getBodyClass() {
        var template = pageProperties.get("cq:template", "");
        var parts = template.split("/");
        var size = parts.length;
        return parts[size-1] + ' ezpizee';
    }

    var parts = currentPage.getLanguage(false).getLanguage().split('_');
    page.lang = parts[0];
    page.isError = isErrorPage();
    page.bodyClass = getBodyClass();
    page.title = pageProperties.get("jcr:title", "");

    return page;
});