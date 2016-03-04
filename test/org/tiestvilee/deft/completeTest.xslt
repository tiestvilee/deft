<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        <!ENTITY copy "&#169;">
        ]>
<xsl:stylesheet version="2.0"
                xmlns:meta="http://www.springer.com/app/meta"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
    <xsl:import href="APlusPlus2HTML_Int_Core.xslt"/>
    <xsl:import href="APlusPlus2HTML_Int_Article.xslt"/>
    <xsl:import href="APlusPlus2HTML_Int_Chapter.xslt"/>
    <xsl:output doctype-system="about:legacy-compat" encoding="UTF-8" indent="no" method="xml"
                omit-xml-declaration="yes"/>

    <!-- ============================================================= -->
    <!-- Templates that can be called externally (e.g. by Casper)      -->
    <!-- ============================================================= -->

    <!-- renders the context information (also called citation)writeForeignLanguageHeader of the article/chapter -->
    <xsl:template name="renderContextInformation">
        <xsl:for-each select="//ArticleInfo">
            <xsl:call-template name="ArticleContextInformation"/>
            <xsl:apply-templates select="ArticleHistory"/>
        </xsl:for-each>
        <xsl:for-each select="//ChapterInfo">
            <xsl:call-template name="ChapterContextInformation"/>
        </xsl:for-each>
    </xsl:template>

    <!-- renders article/chapter category and subcategory -->
    <xsl:template name="renderCategories">
        <xsl:if test="//ArticleCategory | //ArticleSubCategory | //ChapterCategory | //ChapterSubCategory">
            <div class="Categories">
                <xsl:apply-templates
                        select="//ArticleCategory | //ArticleSubCategory | //ChapterCategory | //ChapterSubCategory"/>
                <xsl:call-template name="renderOpenAccessLabel"/>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="renderOpenAccessLabel">
        <xsl:if test="exists(Publisher/meta:Info/meta:OpenAccess)">
            <xsl:choose>
                <xsl:when test="//meta:OpenAccess='true'">
                    <div class="OpenAccessLabel">Open Access</div>
                </xsl:when>
            </xsl:choose>
        </xsl:if>
    </xsl:template>


    <!-- renders the supertitle, title, and subtitle in the main language of the article/chapter -->
    <xsl:template name="renderMainTitleSection">
        <div class="MainTitleSection">
            <xsl:choose>
                <xsl:when test="//ArticleInfo">
                    <xsl:apply-templates select="//ArticleInfo/ArticleSuperTitle[@Language=$mainLanguage]"/>
                    <xsl:apply-templates select="//ArticleInfo/ArticleTitle[@Language=$mainLanguage]"/>
                    <xsl:apply-templates select="//ArticleInfo/ArticleSubTitle[@Language=$mainLanguage]"/>
                </xsl:when>
                <xsl:when test="//ChapterInfo/ChapterTitle[@Language=$mainLanguage]">
                    <xsl:apply-templates select="//ChapterInfo/ChapterTitle[@Language=$mainLanguage]"/>
                    <xsl:apply-templates select="//ChapterInfo/ChapterSubTitle[@Language=$mainLanguage]"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//ChapterInfo/ChapterTitle"/>
                    <xsl:apply-templates select="//ChapterInfo/ChapterSubTitle"/>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

    <!-- renders the complete author group of the article/chapter, consisting of three groups:
    author names, affiliations, contact information -->
    <xsl:template name="renderAuthorGroup">
        <xsl:for-each select="//ArticleHeader | //ChapterHeader">
            <xsl:apply-templates select="AuthorGroup"/>
        </xsl:for-each>
    </xsl:template>

    <!-- renders the author names (with superscript numbers referring to the corresponding affiliations),
     plus a legend for the "deceased" and "contributed equally" symbols (only if they occur) -->
    <xsl:template name="renderAuthorNames">
        <xsl:for-each select="//ArticleHeader/AuthorGroup | //ChapterHeader/AuthorGroup">
            <div class="AuthorNames">
                <ul class="u-listReset">
                    <xsl:apply-templates select="Author | InstitutionalAuthor"/>
                </ul>
                <xsl:if test="descendant::*[@Deceased='Yes' or @EqualContribution='Yes']">
                    <div class="AffiliationLegend">
                        <xsl:if test="descendant::*[@Deceased='Yes']">
                            <div class="AffiliationLegendLine">
                                <span class="DeceasedSymbol">d</span>
                                <xsl:call-template name="printText">
                                    <xsl:with-param name="text" select="'Deceased'"/>
                                </xsl:call-template>
                            </div>
                        </xsl:if>
                        <xsl:if test="descendant::*[@EqualContribution='Yes']">
                            <div class="AffiliationLegendLine">
                                <span class="EqualContributionSymbol">e</span>
                                <xsl:call-template name="printText">
                                    <xsl:with-param name="text" select="'EqualContribution'"/>
                                </xsl:call-template>
                            </div>
                        </xsl:if>
                    </div>
                </xsl:if>
            </div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="Author | Editor | InstitutionalAuthor | InstitutionalEditor">
        <xsl:choose>
            <xsl:when test="ancestor::Loc and Biography/FormalPara">
                <!-- do not render the author name since it is contained in Biography/FormalPara/Heading -->
            </xsl:when>
            <xsl:otherwise>
                <li class="{name()}" data-jumpto="Aff">
                    <xsl:apply-templates
                            select="AuthorName | EditorName | InstitutionalAuthorName | InstitutionalEditorName"/>
                    <xsl:choose>
                        <xsl:when test="(position() = last()-1) and ($mainLanguage!='Zh')">
                            <xsl:text> </xsl:text>
                            <xsl:call-template name="printText">
                                <xsl:with-param name="text" select="'and'"/>
                            </xsl:call-template>
                            <xsl:text> </xsl:text>
                        </xsl:when>
                        <xsl:when test="position() != last()">
                            <xsl:text>, </xsl:text>
                        </xsl:when>
                    </xsl:choose>
                    <div class="AuthorName_tooltip">
                        <div class="Tooltip">
                            <h3 class="Tooltip_subHeading">Affiliated with</h3>
                            <div class="Tooltip_body">
                                <ul class="u-listReset"/>
                            </div>
                        </div>
                    </div>
                </li>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="ancestor::Loc and Biography">
            <xsl:apply-templates select="Biography"/>
        </xsl:if>
    </xsl:template>

    <!-- The display consists of four parts:
         a. The actual name, b. superscript figures for the affiliations,
         c. symbols for "deceased" and "equal contribution", d. contact icon -->
    <xsl:template match="AuthorName | InstitutionalAuthorName">
        <!-- a. render the actual name -->
        <xsl:choose>
            <xsl:when test="name() = 'AuthorName'">
                <xsl:call-template name="renderAuthorEditorCollaboratorName"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="(../@AffiliationIDS or ../Contact or ../@Deceased='Yes' or ../@EqualContribution='Yes')
		    and not(ancestor::BookHeader or ancestor::LocEntry or ancestor::BodyFrontmatter)">
            <!-- supress affiliation numbers and contact icon for the above environments -->
            <!-- c. render symbols "deceased"/"equal contribution" -->
            <xsl:if test="../@Deceased='Yes'">
                <span class="DeceasedSymbol">&#94;</span>
            </xsl:if>
            <xsl:if test="../@EqualContribution='Yes'">
                <span class="EqualContributionSymbol">&#8224;</span>
            </xsl:if>
            <sup>
                <!-- b. render the affiliation numbers -->
                <xsl:call-template name="renderAffiliationIds">
                    <xsl:with-param name="affiliationIdList">
                        <xsl:value-of select="../@AffiliationIDS"/>
                        <xsl:if test="../@PresentAffiliationID and not(contains(../@AffiliationIDS, ../@PresentAffiliationID))">
                            <xsl:text> </xsl:text>
                            <xsl:value-of select="../@PresentAffiliationID"/>
                        </xsl:if>
                    </xsl:with-param>
                </xsl:call-template>
            </sup>
            <!-- d. render the contact icon -->
            <xsl:choose>
                <xsl:when test="../Contact">
                    <xsl:variable name="ContactID">
                        <xsl:value-of select="count(preceding::*[name()='Author' or name()='InstitutionalAuthor'])+1"/>
                    </xsl:variable>
                    <xsl:if test="../Contact/Email and ../@CorrespondingAffiliationID">
                        <xsl:variable name="contactEmail" select="../Contact/Email"/>
                        <a class="EmailAuthor" href="mailto:{$contactEmail}" title="Email author">
                            <span class="ContactIcon" aria-hidden="true"></span>
                            <span class="u-srOnly">Email author</span>
                        </a>
                    </xsl:if>
                </xsl:when>
                <xsl:when test="../@CorrespondingAffiliationID">
                    <span class="ContactIcon"></span>
                </xsl:when>
            </xsl:choose>
        </xsl:if>
        <xsl:if test="../@ORCID">
            <a class="Orcid" title="View ORCID ID profile" href="{../@ORCID}" target="_blank">
                <span class="OrcidIcon" aria-hidden="true"></span>
                <span class="u-srOnly">View ORCID ID profile</span>
            </a>
        </xsl:if>
    </xsl:template>

    <!-- renders the affiliations -->
    <xsl:template name="renderAffiliations">
        <xsl:for-each select="//ArticleHeader/AuthorGroup | //ChapterHeader/AuthorGroup">
            <xsl:if test="Affiliation">
                <section class="Section1 RenderAsSection1 Affiliations" id="Aff">
                    <h2 class="Heading js-ToggleCollapseSection">Authors’ Affiliations</h2>
                    <div class="js-CollapseSection">
                        <xsl:apply-templates select="Affiliation"/>
                    </div>
                </section>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- renders the affiliations ID's -->
    <xsl:template name="renderAffiliationIds">
        <xsl:param name="affiliationIdList"/>
        <xsl:variable name="affiliationIds" select="concat(normalize-space($affiliationIdList), ' ')"/>
        <xsl:variable name="affiliationIdFirst" select="substring-before($affiliationIds, ' ')"/>
        <xsl:variable name="affiliationIdsRest" select="substring-after($affiliationIds, ' ')"/>
        <xsl:for-each select="//ChapterHeader/AuthorGroup/Affiliation | //ArticleHeader/AuthorGroup/Affiliation |
			//Section1/AuthorGroup/Affiliation | //Section2/AuthorGroup/Affiliation |
			//Section3/AuthorGroup/Affiliation">
            <xsl:if test="@ID = $affiliationIdFirst">
                <a href="#{@ID}" class="AffiliationID">
                    <xsl:number count="ChapterHeader/AuthorGroup/Affiliation | ArticleHeader/AuthorGroup/Affiliation |
					Section1/AuthorGroup/Affiliation | Section2/AuthorGroup/Affiliation |
					Section3/AuthorGroup/Affiliation" level="any" format="1"/>
                </a>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="$affiliationIdsRest">
            <xsl:text>, </xsl:text>
            <xsl:call-template name="renderAffiliationIds">
                <xsl:with-param name="affiliationIdList" select="$affiliationIdsRest"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- renders the contact information (for all authors that have those) -->
    <xsl:template name="renderContacts">
        <xsl:for-each select="//ArticleHeader/AuthorGroup | //ChapterHeader/AuthorGroup">
            <xsl:if test="*/Contact">
                <div class="Contacts">
                    <xsl:apply-templates select="*/Contact"/>
                </div>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- renders the abstract in the main language of the article/chapter -->
    <xsl:template name="renderMainAbstract">
        <xsl:apply-templates select="//Abstract[@Language=$mainLanguage]"/>
    </xsl:template>

    <!-- renders the keywords in the main language of the article/chapter -->
    <xsl:template name="renderMainKeywords">
        <xsl:apply-templates select="//KeywordGroup[@Language=$mainLanguage]"/>
    </xsl:template>

    <!-- render titles, abstracts and keywords that are not in the main language of the article/chapter -->
    <xsl:template name="renderForeignLanguageSection">
        <xsl:for-each select="//ArticleHeader | //ChapterHeader">
            <xsl:call-template name="writeForeignLanguageSection1"/>
        </xsl:for-each>
    </xsl:template>

    <!-- renders an abbreviation group -->
    <xsl:template name="renderAbbreviationGroup">
        <xsl:apply-templates select="//AbbreviationGroup"/>
    </xsl:template>

    <!-- renders all article notes occurring in the Article/ChapterHeader (except ProofNote) including joint first authorship, electronic supplementary material, etc -->
    <xsl:template name="renderHeaderArticleNotes">
        <xsl:for-each select="//ArticleHeader | //ChapterHeader">
            <xsl:apply-templates select="ArticleNote[@Type!='ProofNote']"/>
        </xsl:for-each>
    </xsl:template>

    <!-- renders all article notes occurring in the Article/ChapterHeader (except ProofNote) -->
    <xsl:template name="renderArticleNotes">
        <xsl:if test="//ArticleHeader/ArticleNote | //ChapterHeader/ArticleNote and not(//ArticleHeader/ArticleNote[@Type='ESMHint']) and not(//ArticleHeader/ArticleNote[@Type='ProofNote']) and not(//ChapterHeader/ArticleNote[@Type='ESMHint']) and not(//ChapterHeader/ArticleNote[@Type='ProofNote'])">
            <section class="Section1 RenderAsSection1 ArticleNotes" id="ArticleNotes">
                <h2 class="Heading js-ToggleCollapseSection">Notes</h2>
                <div class="js-CollapseSection">
                    <xsl:for-each select="//ArticleHeader | //ChapterHeader">
                        <xsl:apply-templates select="ArticleNote[@Type!='ProofNote']"/>
                    </xsl:for-each>
                </div>
            </section>
        </xsl:if>
    </xsl:template>

    <!-- renders all footnotes occurring in ArticleInfo/ArticleHeader (resp. ChapterInfo/ChapterHeader) -->
    <xsl:template name="renderHeaderFootnotes">
        <xsl:if test="//*[name()='ArticleInfo' or name()='ArticleHeader' or name()='ChapterInfo' or name()='ChapterHeader']//Footnote[not(ancestor::CharacteristicValue) and not(ancestor::Sidebar[@Type='Lexicon']) and not(ancestor::*[@OutputMedium='Paper'])]">
            <div class="FootnoteSection">
                <div class="Heading">
                    <xsl:call-template name="printText">
                        <xsl:with-param name="text" select="'footnotes'"/>
                    </xsl:call-template>
                </div>
                <xsl:for-each
                        select="//*[name()='ArticleInfo' or name()='ArticleHeader' or name()='ChapterInfo' or name()='ChapterHeader']//Footnote[not(ancestor::CharacteristicValue) and not(ancestor::Sidebar[@Type='Lexicon']) and not(ancestor::*[@OutputMedium='Paper'])]">
                    <xsl:call-template name="FootnoteSection2"/>
                </xsl:for-each>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- renders all footnotes -->
    <xsl:template name="renderFootnotes">
        <xsl:for-each select="//Article | //Chapter">
            <xsl:call-template name="FootnoteSection"/>
        </xsl:for-each>
    </xsl:template>

    <!-- renders the body of an article or chapter -->
    <xsl:template name="renderBody">
        <xsl:choose>
            <xsl:when test="//ArticleInfo[@ArticleType='Letter']">
                <section class="Section1 RenderAsSection1">
                    <xsl:apply-templates select="//Body"/>
                </section>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="//Body"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- renders the backmatter of an article or chapter -->
    <xsl:template name="renderBackmatter">
        <xsl:for-each select="//ArticleBackmatter | //ChapterBackmatter">
            <xsl:if test="ArticleNote | Acknowledgments | Ethics | //ArticleCopyright/License | //ChapterCopyright/License | Glossary | Appendix">
                <section class="Section1 RenderAsSection1" id="Declarations">
                    <h2 class="Heading js-ToggleCollapseSection">
                        Declarations
                    </h2>
                    <div class="js-CollapseSection">
                        <xsl:apply-templates select="ArticleNote[@Type!='Misc']"/>
                        <xsl:apply-templates select="Acknowledgments"/>
                        <xsl:apply-templates select="Ethics"/>
                        <xsl:apply-templates select="//ArticleCopyright/License | //ChapterCopyright/License"/>
                        <xsl:apply-templates select="Glossary"/>
                        <xsl:apply-templates select="Appendix"/>
                        <xsl:apply-templates select="ArticleNote[@Type='Misc']"/>
                    </div>
                </section>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- renders the bibliography -->
    <xsl:template name="renderBibliography">
        <xsl:apply-templates select="//Bibliography"/>
    </xsl:template>

    <xsl:param name="dds_id" select="''"/>

    <xsl:variable name="encoded_dds_id">
        <xsl:call-template name="encodeDdsId">
            <xsl:with-param name="value1" select="$dds_id"/>
        </xsl:call-template>
    </xsl:variable>

    <xsl:template name="encodeDdsId">
        <xsl:param name="value1"/>
        <xsl:variable name="value2">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="$value1"/>
                <xsl:with-param name="replace" select="'['"/>
                <xsl:with-param name="with" select="'%5B'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="value3">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="$value2"/>
                <xsl:with-param name="replace" select="']'"/>
                <xsl:with-param name="with" select="'%5D'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="value4">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="$value3"/>
                <xsl:with-param name="replace" select="':'"/>
                <xsl:with-param name="with" select="'%3A'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="value5">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="$value4"/>
                <xsl:with-param name="replace" select="';'"/>
                <xsl:with-param name="with" select="'%3B'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="value6">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="$value5"/>
                <xsl:with-param name="replace" select="' '"/>
                <xsl:with-param name="with" select="'%20'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="value7">
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text" select="$value6"/>
                <xsl:with-param name="replace" select="'/'"/>
                <xsl:with-param name="with" select="'%2F'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$value7"/>
    </xsl:template>

    <xsl:template name="replace-string">
        <xsl:param name="text"/>
        <xsl:param name="replace"/>
        <xsl:param name="with"/>
        <xsl:choose>
            <xsl:when test="contains($text,$replace)">
                <xsl:value-of select="substring-before($text,$replace)"/>
                <xsl:value-of select="$with"/>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text" select="substring-after($text,$replace)"/>
                    <xsl:with-param name="replace" select="$replace"/>
                    <xsl:with-param name="with" select="$with"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="Stack">
        <span class="stack">
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:variable name="mainLanguage">
        <xsl:choose>
            <xsl:when test="//AuthorFragment/@Language">
                <xsl:value-of select="//AuthorFragment/@Language"/>
            </xsl:when>
            <xsl:when test="//ArticleInfo/@Language">
                <xsl:value-of select="//ArticleInfo/@Language"/>
            </xsl:when>
            <xsl:when test="//SubPart[1]/Chapter[1]/@Language">
                <xsl:value-of select="//SubPart[1]/Chapter[1]/@Language"/>
            </xsl:when>
            <xsl:when test="//Chapter[1]/@Language">
                <xsl:value-of select="//Chapter[1]/@Language"/>
            </xsl:when>
            <xsl:otherwise> <!-- default for FM/BM which don't contain ArticleInfo or Chapter -->
                <xsl:value-of select="//BookInfo/@Language"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:template match="Abstract">
        <section class="Abstract" id="{@ID}">
            <xsl:call-template name="addLanguageAttribute">
                <xsl:with-param name="language" select="@Language"/>
            </xsl:call-template>
            <xsl:apply-templates select="Heading[1]"/>
            <div class="js-CollapseSection">
                <xsl:apply-templates select="*[not(self::Heading[1])]"/>
                <xsl:call-template name="renderMainKeywords"/>
            </div>
        </section>
    </xsl:template>

    <xsl:template match="AbbreviationGroup">
        <section class="AbbreviationGroup">
            <xsl:apply-templates select="Heading[1]"/>
            <div class="js-CollapseSection">
                <xsl:apply-templates select="*[not(self::Heading[1])]"/>
            </div>
        </section>
    </xsl:template>

    <xsl:template match="Section2 | Section3 | Section4 | Section5 | Section6 | Section7">
        <section id="{@ID}">
            <xsl:attribute name="class">
                <xsl:value-of select="name()"/>
                <xsl:text> RenderAs</xsl:text>
                <xsl:choose>
                    <xsl:when test="@RenderAs">
                        <xsl:value-of select="@RenderAs"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="name()"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="@Type">
                    <xsl:text> SectionType</xsl:text>
                    <xsl:value-of select="@Type"/>
                </xsl:if>
            </xsl:attribute>
            <xsl:apply-templates/>
        </section>
    </xsl:template>


    <xsl:template match="Section1">
        <xsl:choose>
            <xsl:when test="not(parent::Appendix)">
                <section id="{@ID}">
                    <xsl:attribute name="class">
                        <xsl:value-of select="name()"/>
                        <xsl:text> RenderAs</xsl:text>
                        <xsl:choose>
                            <xsl:when test="@RenderAs">
                                <xsl:value-of select="@RenderAs"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="name()"/>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="@Type">
                            <xsl:text> SectionType</xsl:text>
                            <xsl:value-of select="@Type"/>
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:apply-templates select="Heading[1]"/>
                    <div class="js-CollapseSection">
                        <xsl:apply-templates select="*[not(self::Heading[1])]"/>
                    </div>
                </section>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="Caption">
        <xsl:choose>
            <xsl:when test="string-length(CaptionContent/SimplePara[1])=0">
                <!-- don't output anything if the caption is empty -->
            </xsl:when>
            <xsl:when
                    test="parent::DataObject or parent::VideoObject[not(@VideoID)] or parent::AudioObject or matches(parent::ImageObject/@FileRef, 'ESM')">
                <span class="Caption">
                    <xsl:call-template name="Caption2"/>
                </span>
            </xsl:when>
            <xsl:when test="parent::Figure or parent::Table">
                <figcaption class="Caption">
                    <xsl:call-template name="Caption2"/>
                </figcaption>
            </xsl:when>
            <xsl:otherwise>
                <div class="Caption">
                    <xsl:call-template name="Caption2"/>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="Figure">
        <xsl:param name="biographyFirst" select="'false'"/>
        <xsl:choose>
            <xsl:when test="ancestor::Biography and $biographyFirst='false'">
                <!-- In this case, do not output the figure. It is handled separately
                within the Biography template -->
            </xsl:when>
            <xsl:when test="ancestor::Abstract">
                <figure class="Figure" id="{@ID}">
                    <xsl:apply-templates select="MediaObject"/>
                </figure>
            </xsl:when>
            <!--xsl:when test="ancestor::CaptionContent">
              <span class="Figure" id="{@ID}">
                <xsl:apply-templates select="MediaObject" />
                <xsl:apply-templates select="Caption" />
              </span>
            </xsl:when-->
            <xsl:otherwise>
                <figure class="Figure" id="{@ID}">
                    <xsl:apply-templates select="MediaObject"/> <!-- do not combine into one line since          -->
                    <xsl:apply-templates select="Caption"/>     <!-- MediaObject must be rendered before Caption -->
                </figure>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="Table">
        <xsl:param name="context" select="''" tunnel="yes"/>
        <xsl:variable name="TabID" select="@ID"/>
        <figure id="{@ID}">
            <xsl:attribute name="class">
                <xsl:text>FigureTable</xsl:text>
                <xsl:if test="$context!='Tables' and $external_tables='true' and not(ancestor::Table)">
                    <xsl:text> ExternalTable</xsl:text>
                </xsl:if>
            </xsl:attribute>
            <xsl:choose>
                <xsl:when test="$context!='Tables' and $external_tables='true' and not(ancestor::Table)">
                    <xsl:choose>
                        <xsl:when test="Caption">
                            <xsl:apply-templates select="Caption"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="filename">
                                <xsl:call-template name="buildTableFilename"/>
                            </xsl:variable>
                            <figcaption class="Caption">
                                <div class="CaptionContent">
                                    <a href="{$filename}">
                                        <span class="CaptionNumber">
                                            <xsl:call-template name="printText">
                                                <xsl:with-param name="text" select="'table'"/>
                                            </xsl:call-template>
                                        </span>
                                    </a>
                                </div>
                            </figcaption>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="Caption"/>
                    <div class="Table">
                        <xsl:apply-templates select="MediaObject | tgroup"/>
                    </div>
                    <xsl:apply-templates select="tfooter"/>

                </xsl:otherwise>
            </xsl:choose>
        </figure>
    </xsl:template>

    <!-- This template constructs the filename for the standalone
  table files, which are written when external_tables == 'true'. -->
    <xsl:template name="buildTableFilename">
        <xsl:param name="table"/>
        <xsl:text>Table_</xsl:text>
        <xsl:choose> <!-- Part number. Use 00 for FM, 99 for BM, and 01 if there are no parts -->
            <xsl:when test="//PartSequenceNumber">
                <xsl:value-of select='format-number(//PartSequenceNumber, "00")'/>
            </xsl:when>
            <xsl:when test="//BookFrontmatter">00</xsl:when>
            <xsl:when test="//BookBackmatter">99</xsl:when>
            <xsl:otherwise>01</xsl:otherwise>
        </xsl:choose>
        <xsl:text>_</xsl:text>
        <xsl:choose> <!-- Chapter numner. Use 0000 for FM and 9999 for BM -->
            <xsl:when test="ancestor::Chapter//ChapterSequenceNumber">
                <xsl:value-of select='format-number(ancestor::Chapter//ChapterSequenceNumber, "0000")'/>
            </xsl:when>
            <xsl:when test="//BookFrontmatter">0000</xsl:when>
            <xsl:when test="//BookBackmatter">9999</xsl:when>
            <xsl:otherwise>0001</xsl:otherwise> <!-- this should not happen -->
        </xsl:choose>
        <xsl:text>_</xsl:text>
        <xsl:number format="0001" count="Table" level="any"/>
        <xsl:text>.xhtml</xsl:text>
    </xsl:template>

    <!-- Ordered List -->
    <xsl:template match="OrderedList">
        <ol class="OrderedList">
            <xsl:apply-templates select="Heading"/>
            <xsl:for-each select="ListItem">
                <li class="ListItem">
                    <xsl:apply-templates select="ItemNumber | ItemContent"/>
                    <div class="ClearBoth">&#160;</div>
                </li>
            </xsl:for-each>
        </ol>
    </xsl:template>


    <xsl:template match="Bibliography">
        <section class="Section1 RenderAsSection1 SectionTypeMaterialsAndMethods" id="{@ID}">
            <xsl:apply-templates select="Heading"/>
            <div class="js-CollapseSection">
                <ol class="BibliographyWrapper">
                    <xsl:apply-templates select="BibSection | Citation"/>
                </ol>
            </div>
        </section>
    </xsl:template>

    <xsl:template match="Citation">
        <li class="Citation">
            <cite class="CitationContent" id="{@ID}">
                <xsl:choose>
                    <xsl:when test="BibUnstructured">
                        <xsl:apply-templates select="BibUnstructured"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="BibArticle | BibChapter | BibIssue | BibBook"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test=".//Occurrence">
                    <span class="Occurrences">
                        <xsl:apply-templates select=".//Occurrence"/>
                    </span>
                </xsl:if>
            </cite>
        </li>
    </xsl:template>

    <xsl:template name="RenderAffiliationIds">
        <xsl:param name="affiliationIdList"/>
        <xsl:variable name="affiliationIds" select="concat(normalize-space($affiliationIdList), ' ')"/>
        <xsl:variable name="affiliationIdFirst" select="substring-before($affiliationIds, ' ')"/>
        <xsl:variable name="affiliationIdsRest" select="substring-after($affiliationIds, ' ')"/>
        <xsl:for-each select="//AuthorFragment/AuthorGroup/Affiliation | //ChapterHeader/AuthorGroup/Affiliation | //ArticleHeader/AuthorGroup/Affiliation |
			//Section1/AuthorGroup/Affiliation | //Section2/AuthorGroup/Affiliation |
			//Section3/AuthorGroup/Affiliation">
            <xsl:if test="@ID = $affiliationIdFirst">
                <xsl:number count="//AuthorFragment/AuthorGroup/Affiliation | ChapterHeader/AuthorGroup/Affiliation | ArticleHeader/AuthorGroup/Affiliation |
					Section1/AuthorGroup/Affiliation | Section2/AuthorGroup/Affiliation |
					Section3/AuthorGroup/Affiliation" level="any" format="1"/>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="$affiliationIdsRest">
            <xsl:text>, </xsl:text>
            <xsl:call-template name="RenderAffiliationIds">
                <xsl:with-param name="affiliationIdList" select="$affiliationIdsRest"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="Affiliation">
        <xsl:param name="firstOccurrenceOfAffiliation" select="'true'" tunnel="yes"/>
        <xsl:param name="onCopyrightPage" select="'false'"/>
        <div class="Affiliation">
            <xsl:if test="$firstOccurrenceOfAffiliation='true'">
                <xsl:attribute name="id" select="@ID"/>
            </xsl:if>
            <xsl:if test="not(ancestor::BookHeader) and not(ancestor::BookFrontmatter)">
                <span class="AffiliationNumber">
                    <xsl:text>(</xsl:text>
                    <xsl:number count="AuthorFragment/AuthorGroup/Affiliation | ChapterHeader/AuthorGroup/Affiliation |
							ArticleHeader/AuthorGroup/Affiliation | Section1/AuthorGroup/Affiliation |
							Section2/AuthorGroup/Affiliation | Section3/AuthorGroup/Affiliation"
                                level="any" format="1"/>
                    <xsl:text>)</xsl:text>
                </span>
            </xsl:if>
            <div class="AffiliationText">
                <xsl:if test="OrgDivision !=''">
                    <xsl:apply-templates select="OrgDivision"/>
                    <xsl:text>, </xsl:text>
                </xsl:if>
                <xsl:if test="OrgName !=''">
                    <xsl:apply-templates select="OrgName"/>
                </xsl:if>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="RenderAuthorEditorCollaboratorName">
        <span class="{name()}">
            <xsl:if test="ancestor::Preface or ancestor::Foreword">
                <xsl:apply-templates select="Prefix"/>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="@DisplayOrder='Western'">
                    <xsl:call-template name="RenderGivenNameAndParticle"/>
                    <xsl:apply-templates select="FamilyName"/>
                </xsl:when>
                <xsl:otherwise> <!-- DisplayOrder='Eastern' -->
                    <xsl:apply-templates select="FamilyName"/>
                    <xsl:if test="$mainLanguage != 'Zh'">
                        <xsl:text>&nbsp;</xsl:text> <!-- don't separate family and given name by space for names in Chinese -->
                    </xsl:if>
                    <xsl:call-template name="RenderGivenNameAndParticle"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="Suffix | NativeName"/>
        </span>
    </xsl:template>

    <xsl:template name="RenderGivenNameAndParticle">
        <xsl:param name="currentLanguage"/>
        <xsl:if test="not($currentLanguage = $mainLanguage)">
            <div class="ForeignLanguageHeader">
                <xsl:if test="$src//ArticleInfo/ArticleTitle[@Language=$currentLanguage] |
					$src//ArticleInfo/ArticleSubTitle[@Language=$currentLanguage] |
					$src//ArticleInfo/ArticleSuperTitle[@Language=$currentLanguage] |
					$src//ChapterInfo/ChapterTitle[@Language=$currentLanguage] |
					$src//ChapterInfo/ChapterSubTitle[@Language=$currentLanguage]">
                    <div class="ForeignLanguageTitleSection">
                        <xsl:apply-templates select="$src//ArticleInfo/ArticleSuperTitle[@Language=$currentLanguage]"/>
                        <xsl:apply-templates select="$src//ArticleInfo/ArticleTitle[@Language=$currentLanguage] |
							$src//ChapterInfo/ChapterTitle[@Language=$currentLanguage]"/>
                        <xsl:apply-templates select="$src//ArticleInfo/ArticleSubTitle[@Language=$currentLanguage] |
							$src//ChapterInfo/ChapterSubTitle[@Language=$currentLanguage]"/>
                    </div>
                </xsl:if>
                <xsl:apply-templates select="$src//ArticleHeader/Abstract[@Language=$currentLanguage] |
					$src//ChapterHeader/Abstract[@Language=$currentLanguage]"/>
                <xsl:apply-templates select="$src//ArticleHeader/KeywordGroup[@Language=$currentLanguage] |
					$src//ChapterHeader/KeywordGroup[@Language=$currentLanguage]"/>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="Contact">
        <xsl:variable name="ContactID">
            <xsl:value-of select="count(preceding::*[name()='Author' or name()='InstitutionalAuthor'])+1"/>
        </xsl:variable>
        <xsl:if test="(../@CorrespondingAffiliationID)">
            <div class="Contact">
                <xsl:attribute name="id">
                    <xsl:text>ContactOfAuthor</xsl:text>
                    <xsl:value-of select="$ContactID"/>
                </xsl:attribute>
                <div class="ContactIcon">&nbsp;</div>
                <div class="ContactAuthorLine">
                    <xsl:for-each select="../AuthorName | ../InstitutionalAuthorName">
                        <xsl:choose>
                            <xsl:when test="name() = 'AuthorName'">
                                <xsl:call-template name="RenderAuthorEditorCollaboratorName"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                    <xsl:apply-templates select="../Role"/>
                    <xsl:if test="(count(../../*/Contact) > 1) and (../@CorrespondingAffiliationID)">
                        <xsl:text>&nbsp;(</xsl:text>
                        <xsl:call-template name="printText">
                            <xsl:with-param name="text" select="'CorrespondingAuthor'"/>
                        </xsl:call-template>
                        <xsl:text>)</xsl:text>
                    </xsl:if>
                </div>
                <xsl:if test="*[not(name()='Email' or name()='Phone' or name()='Fax' or name()='URL')]">
                    <div class="ContactAdditionalLine">
                        <xsl:for-each select="*[not(name()='Email' or name()='Phone' or name()='Fax' or name()='URL')]">
                            <xsl:apply-templates select="."/>
                            <xsl:choose>
                                <xsl:when
                                        test="name() != 'Postcode' and following-sibling::*[not(name()='Email' or name()='Phone' or name()='Fax' or name()='URL')]/text()">
                                    <xsl:text>, </xsl:text>
                                </xsl:when>
                                <xsl:when
                                        test="name() = 'Postcode' and following-sibling::*[position()= 1 and name()!='City']/text()">
                                    <xsl:text>, </xsl:text>
                                </xsl:when>
                                <xsl:when test="name() = 'Postcode' and following-sibling::City[position()=1]/text()">
                                    <xsl:text>&nbsp;</xsl:text>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:for-each>
                    </div>
                </xsl:if>
                <xsl:for-each select="Email[not(@OutputMedium) or @OutputMedium!='None']">
                    <div class="ContactAdditionalLine">
                        <span class="ContactType">Email:</span>
                        <xsl:choose>
                            <xsl:when test="@OutputMedium='Paper'">
                                <xsl:call-template name="printText">
                                    <xsl:with-param name="text" select="'notRenderedInHtml'"/>
                                </xsl:call-template>
                            </xsl:when>
                            <xsl:otherwise>
                                <a href="mailto:{.}">
                                    <xsl:value-of select="."/>
                                </a>
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>
                </xsl:for-each>
                <xsl:for-each select="URL">
                    <div class="ContactAdditionalLine">
                        <span class="ContactType">URL:</span>
                        <xsl:value-of select="."/>
                    </div>
                </xsl:for-each>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="determineFileRef">
        <xsl:param name="FileRef"/>

        <xsl:choose>
            <xsl:when test="starts-with($FileRef, 'http')">
                <xsl:value-of select="$FileRef"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="determineAbsoluteFileRef">
                    <xsl:with-param name="FileRef" select="$FileRef"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="determineAbsoluteFileRef">
        <xsl:param name="FileRef"/>

        <xsl:choose>
            <xsl:when test="$media_path != '' and (self::VideoObject or self::AudioObject or self::DataObject
				or self::ImageObject[ancestor::Appendix[@OutputMedium='Online'] and not(ancestor::InlineEquation
				or ancestor::Equation) and matches($FileRef, 'ESM')])">
                <xsl:value-of select="$media_path"/>
            </xsl:when>
            <xsl:when test="$image_path != '' and self::ImageObject">
                <xsl:value-of select="$image_path"/>
            </xsl:when>
        </xsl:choose>
        <xsl:value-of select="$encoded_dds_id"/>/<xsl:value-of select="$FileRef"/>

    </xsl:template>

    <xsl:template match="Emphasis">
        <xsl:choose>
            <xsl:when test="@Type and @FontCategory">
                <span class="EmphasisType{@Type}">
                    <span class="EmphasisFontCategory{@FontCategory}">
                        <xsl:apply-templates/>
                    </span>
                </span>
            </xsl:when>
            <xsl:when test="@Type = 'Bold'">
                <strong class="EmphasisType{@Type}">
                    <xsl:apply-templates/>
                </strong>
            </xsl:when>
            <xsl:when test="@Type = 'Italic'">
                <em class="EmphasisType{@Type}">
                    <xsl:apply-templates/>
                </em>
            </xsl:when>
            <xsl:when test="@Type">
                <span class="EmphasisType{@Type}">
                    <xsl:apply-templates/>
                </span>
            </xsl:when>
            <xsl:when test="@FontCategory">
                <span class="EmphasisFontCategory{@FontCategory}">
                    <xsl:apply-templates/>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- Definition List -->
    <xsl:template match="DefinitionList">
        <div class="DefinitionList">
            <xsl:apply-templates/> <!-- Heading, DefinitionListEntry -->
        </div>
    </xsl:template>

    <xsl:template match="DefinitionListEntry">
        <dl class="DefinitionListEntry">
            <xsl:apply-templates select="Term | Description"/>
        </dl>
    </xsl:template>

    <xsl:template match="Term">
        <dt class="Term">
            <dfn><xsl:apply-templates/>:&nbsp;
            </dfn>
        </dt>
    </xsl:template>

    <xsl:template match="Description">
        <dd class="Description"> <!-- contains Paras, so it must be div -->
            <xsl:apply-templates/>
        </dd>
    </xsl:template>


    <xsl:template match="Occurrence">
        <xsl:param name="encodedHandle">
            <xsl:call-template name="encodeUrl">
                <xsl:with-param name="value1" select="normalize-space(Handle)"/>
            </xsl:call-template>
        </xsl:param>
        <xsl:param name="normalizedUrl" select="normalize-space(URL)"/>
        <xsl:if test="(@Type != 'COI') or ($chemport_reference_handling = 'transitional_links')"> <!-- otherwise do not create a link -->
            <span class="Occurrence Occurrence{@Type}">
                <xsl:choose>
                    <xsl:when test="@Type='AMSID'">
                        <a href="http://www.ams.org/mathscinet-getitem?mr={$encodedHandle}">
                            <span>
                                <span>MathSciNet</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='Bibcode'">
                        <a href="http://adsabs.harvard.edu/cgi-bin/nph-data_query?link_type=ABSTRACT&amp;bibcode={$encodedHandle}">
                            <span>
                                <span>ADS</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='COI'">
                        <a href="http://chemport.cas.org/cgi-bin/sdcgi?APP=ftslink&amp;action=reflink&amp;origin=springer&amp;version=1.0&amp;coi={$encodedHandle}&amp;md5=">
                            <span>
                                <span>ChemPort</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='DOI'">
                        <a href="http://dx.doi.org/{$encodedHandle}">
                            <span>
                                <span>View Article</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='ISIID'">
                        <!-- not supported -->
                    </xsl:when>
                    <xsl:when test="@Type='PID'">
                        <a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;dopt=Abstract&amp;list_uids={$encodedHandle}">
                            <span>
                                <span>PubMed</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='PMCID'">
                        <a href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC{$encodedHandle}">
                            <span>
                                <span>PubMed Central</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='URL'">
                        <a href="{$normalizedUrl}">
                            <span>
                                <span>URL</span>
                            </span>
                        </a>
                    </xsl:when>
                    <xsl:when test="@Type='ZLBID'">
                        <a href="http://www.emis.de/MATH-item?${$encodedHandle}">
                            <span>
                                <span>MATH</span>
                            </span>
                        </a>
                    </xsl:when>
                </xsl:choose>
            </span>
        </xsl:if>
    </xsl:template>

    <xsl:template name="ArticleContextInformation">
        <xsl:param name="context" tunnel="yes"/>
        <div class="ArticleContextInformation">
            <div class="ContextInformation">
                <div class="ContextInformationJournalTitles">
                    <xsl:choose>
                        <xsl:when test="ArticleCitationID">
                            <span class="ArticleCitation">
                                <xsl:apply-templates
                                        select="//JournalInfo/JournalTitle | //JournalInfo/JournalSubTitle"/>
                                <span class="ArticleCitation_Year">
                                    <xsl:apply-templates select="ArticleHistory/OnlineDate/Year"/>
                                </span>

                                <span class="ArticleCitation_Volume">
                                    <strong>
                                        <xsl:apply-templates select="//VolumeInfo/VolumeIDStart"/>
                                        <xsl:if test="//Volume/Issue[@IssueType = 'Supplement']">(Suppl
                                            <xsl:apply-templates select="//Issue/IssueInfo/IssueIDStart"/>)
                                        </xsl:if>
                                    </strong>
                                    :
                                    <xsl:apply-templates select="ArticleCitationID"/>
                                </span>

                            </span>
                        </xsl:when>
                        <xsl:otherwise>
                            <span class="ArticleCitation">
                                <xsl:apply-templates
                                        select="//JournalInfo/JournalTitle | //JournalInfo/JournalSubTitle"/>
                                <span class="ArticleCitation_Year">
                                    <xsl:apply-templates select="ArticleHistory/OnlineDate/Year"/>
                                </span>
                                <span class="ArticleCitation_Volume">
                                    <strong>
                                        <xsl:apply-templates select="//VolumeInfo/VolumeIDStart "/>
                                    </strong>
                                    :
                                    <xsl:apply-templates select="ArticleID"/>
                                </span>
                            </span>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
                <p class="ArticleDOI">
                    <strong>DOI:</strong>
                    <xsl:value-of select="ArticleDOI"/>
                </p>
                <p class="Copyright">
                    <xsl:text>&copy; &nbsp;</xsl:text>
                    <xsl:apply-templates
                            select="//ArticleCopyright/CopyrightHolderName | //ChapterCopyright/CopyrightHolderName"/>
                    <xsl:text>&nbsp;</xsl:text>
                    <xsl:apply-templates select="//ArticleCopyright/CopyrightYear | //ChapterCopyright/CopyrightYear"/>
                </p>
            </div>
        </div>
    </xsl:template>


    <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
    <!-- ArticleHeader                                                 -->
    <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
    <xsl:template match="ArticleHeader">
        <xsl:param name="context" select="''" tunnel="yes"/>
        <xsl:variable name="foreignLangList">
            <xsl:value-of
                    select="distinct-values(//*[name()='ArticleInfo' or name()='ArticleHeader' or name()='ChapterInfo' or name()='ChapterHeader']//@Language)"/>
        </xsl:variable>
        <xsl:variable name="tokenizedForeignLangList" select="tokenize($foreignLangList,' ')"/>
        <xsl:apply-templates select="AuthorGroup"/>
        <xsl:apply-templates select="../ArticleInfo/ArticleHistory"/>
        <xsl:apply-templates select="//ArticleCopyright"/>
        <xsl:for-each select="PageHeaders/OpeningFigure">
            <div class="OpeningFigure">
                <xsl:call-template name="Figures"/>
            </div>
        </xsl:for-each>
        <xsl:apply-templates select="ArticleNote[@Type = 'CommunicatedBy']"/>
        <xsl:apply-templates select="AuthorGroup/Author/Figure"/>
        <xsl:if test="//ArticleInfo/@TocLevels !='0' and $context!='Abstract' and //Body">
            <xsl:call-template name="ArticleOrChapterToc">
                <xsl:with-param name="insideArticleOrChapterToc" select="'true'" tunnel="yes"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:choose>
            <xsl:when test="Abstract">
                <xsl:apply-templates select="Abstract[@Language=$mainLanguage]"/>
            </xsl:when>
            <xsl:when test="//JournalInfo/@JournalProductType='Magazine' or $epub_output='true'">
                <!-- don't output anything -->
            </xsl:when>
            <xsl:otherwise>
                <div class="Abstract">
                    <xsl:call-template name="addLanguageAttribute">
                        <xsl:with-param name="language" select="$mainLanguage"/>
                    </xsl:call-template>
                    <div class="Heading">
                        <xsl:call-template name="printText">
                            <xsl:with-param name="text" select="'withoutAbstract'"/>
                        </xsl:call-template>
                    </div>
                </div>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="ArticleNote[@Type = 'ESMHint']"/>
        <xsl:apply-templates select="KeywordGroup[@Language=$mainLanguage]"/>
        <!-- Foreign Language Header Information -->
        <xsl:for-each select="$tokenizedForeignLangList">
            <xsl:call-template name="writeForeignLanguageHeader">
                <xsl:with-param name="currentLanguage" select="."/>
            </xsl:call-template>
        </xsl:for-each>
        <xsl:if test="$context!='Abstract'">
            <xsl:apply-templates select="AbbreviationGroup"/>
        </xsl:if>
        <xsl:apply-templates
                select="ArticleNote[@Type != 'ESMHint' and @Type != 'ProofNote' and @Type != 'CommunicatedBy']"/>
        <xsl:if test="$context='Abstract'">
            <xsl:apply-templates select="AuthorGroup//Contact"/>
        </xsl:if>
        <xsl:if test="AuthorGroup//Biography">
            <div class="Biographies">
                <xsl:for-each select="AuthorGroup//Biography">
                    <xsl:apply-templates select="."/>
                </xsl:for-each>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="writeForeignLanguageHeader">
        <xsl:param name="currentLanguage"/>
        <xsl:if test="not($currentLanguage = $mainLanguage)">
            <div class="ForeignLanguageHeader">
                <xsl:if test="$src//ArticleInfo/ArticleTitle[@Language=$currentLanguage] |
					$src//ArticleInfo/ArticleSubTitle[@Language=$currentLanguage] |
					$src//ArticleInfo/ArticleSuperTitle[@Language=$currentLanguage] |
					$src//ChapterInfo/ChapterTitle[@Language=$currentLanguage] |
					$src//ChapterInfo/ChapterSubTitle[@Language=$currentLanguage]">
                    <div class="ForeignLanguageTitleSection">
                        <xsl:apply-templates select="$src//ArticleInfo/ArticleSuperTitle[@Language=$currentLanguage]"/>
                        <xsl:apply-templates select="$src//ArticleInfo/ArticleTitle[@Language=$currentLanguage] |
							$src//ChapterInfo/ChapterTitle[@Language=$currentLanguage]"/>
                        <xsl:apply-templates select="$src//ArticleInfo/ArticleSubTitle[@Language=$currentLanguage] |
							$src//ChapterInfo/ChapterSubTitle[@Language=$currentLanguage]"/>
                    </div>
                </xsl:if>
                <xsl:apply-templates select="$src//ArticleHeader/Abstract[@Language=$currentLanguage] |
					$src//ChapterHeader/Abstract[@Language=$currentLanguage]"/>
                <xsl:apply-templates select="$src//ArticleHeader/KeywordGroup[@Language=$currentLanguage] |
					$src//ChapterHeader/KeywordGroup[@Language=$currentLanguage]"/>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="SimplePara">
        <xsl:param name="inlineGlossaryDefinition" tunnel="yes"/>
        <xsl:variable name="Class">
            <xsl:text>SimplePara</xsl:text>
            <xsl:if test="@Type">
                <xsl:text> ParaType</xsl:text>
                <xsl:value-of select="@Type"/>
            </xsl:if>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="string-length(.)=0 and not(child::*)">
                <!-- don't output anything if the SimplePara is empty -->
            </xsl:when>
            <xsl:when test="parent::CaptionContent/ancestor::DataObject
			    or parent::CaptionContent/ancestor::VideoObject[not(@VideoID)] or parent::CaptionContent/ancestor::AudioObject
			    or matches(parent::CaptionContent/ancestor::ImageObject/@FileRef, 'ESM') or $inlineGlossaryDefinition='true'">
                <span class="{$Class}">
                    <xsl:call-template name="SimplePara2"/>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <p class="{$Class}">
                    <xsl:call-template name="SimplePara2"/>
                </p>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
    <!-- ArticleBackmatter / ChapterBackmatter                         -->
    <!--   Appendix                                                    -->
    <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
    <xsl:template match="Appendix">
        <xsl:choose>
            <xsl:when test="$epub_output='true' and $epub_include_esm='false' and @OutputMedium='Online' and
				(ancestor::Chapter/ChapterInfo/@ContainsESM='Yes' or ancestor::Book/BookInfo/@ContainsESM='Yes'
				 or ancestor::Article/ArticleInfo/@ContainsESM='Yes') ">
                <!-- do not include ESM -->
            </xsl:when>
            <xsl:otherwise>
                <aside class="Appendix" id="{@ID}">
                    <xsl:apply-templates/>
                </aside>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- For the ESM, create links to the ImageObjects, VideoObjects, AudioObjects and
       DataObjects, either using the caption, or (if not present) a generic text. ImageObjects
       inside equations are excluded and are displayed as usual. -->
    <xsl:template match="Appendix[@OutputMedium='Online']//ImageObject[not (ancestor::InlineEquation or
		ancestor::Equation)] | AudioObject | DataObject" name="ESMLink">
        <xsl:variable name="FileRef">
            <xsl:call-template name="determineFileRef">
                <xsl:with-param name="FileRef" select="@FileRef"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="FileRef2">
            <xsl:choose>
                <xsl:when
                        test="($epub_output='true') and (matches($FileRef, '(jpg|gif)$')) and (matches($FileRef, 'ESM'))">
                    <xsl:value-of select="concat($FileRef, '.html')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$FileRef"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="FileName">
            <xsl:value-of select="subsequence(reverse(tokenize($FileRef, '/')), 1, 1)"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when
                    test="(name() = 'ImageObject') and (matches($FileRef, '(jpg|gif)$')) and not(matches($FileRef, 'ESM')) ">
                <xsl:call-template name="ImageObject2"/>
            </xsl:when>
            <xsl:when test="(name() = 'VideoObject') and (exists(@VideoID))">
                <xsl:call-template name="ESMExternalVideo"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="item-class">
                    <xsl:choose>
                        <xsl:when test="(name() = 'VideoObject')">type-video</xsl:when>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="(name() = 'AudioObject')">type-audio</xsl:when>
                    </xsl:choose>
                </xsl:variable>
                <div class="esm-item {$item-class}">
                    <xsl:call-template name="ESMCaptionWithLink">
                        <xsl:with-param name="fileRefLink" select="$FileRef2"/>
                        <xsl:with-param name="fileName" select="$FileName"/>
                    </xsl:call-template>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- render a ArticleNavigation -->
    <xsl:template name="renderArticleNavigation">
        <nav id="ArticleNavigation" class="ToC" aria-label="table of contents">
            <div class="Collapse Collapse--button Collapse--buttonToC isOpen" id="collapseToC">
                <h3 class="Collapse_toggle Collapse_toggle--ToC" data-toggle="collapse" data-target="collapseToC"
                    data-event-category="Table of Contents" data-track-event="click"
                    data-event-action="Toggle ToC" data-event-label="Toggle">Table of Contents
                    <span class="Icon Icon--arrow-down-primary"></span>
                </h3>
                <ul class="Collapse_content" role="menu">
                    <xsl:if test="//Abstract">
                        <li role="menuitem">
                            <a href="#{//Abstract/@ID}" data-event-category="Table of Contents" data-track-event="click"
                               data-event-action="Link Clicked" data-event-label="{//Abstract/Heading}">
                                <xsl:value-of select="//Abstract/Heading"></xsl:value-of>
                            </a>
                        </li>
                    </xsl:if>
                    <xsl:for-each select="//Section1[not (ancestor::Appendix)]">
                        <xsl:variable name="ToCID" select="@ID"/>
                        <li role="menuitem">
                            <a href="#{$ToCID}" data-event-category="Table of Contents" data-track-event="click"
                               data-event-action="Link Clicked" data-event-label="{Heading}">
                                <xsl:value-of select="Heading"></xsl:value-of>
                            </a>
                        </li>
                    </xsl:for-each>
                    <xsl:for-each select="//ArticleBackmatter | //ChapterBackmatter">
                        <xsl:if test="ArticleNote | Acknowledgments | Ethics | //ArticleCopyright/License | //ChapterCopyright/License | Glossary | Appendix">
                            <li role="menuitem">
                                <a href="#Declarations" data-event-category="Table of Contents" data-track-event="click"
                                   data-event-action="Link Clicked" data-event-label="Declarations">
                                    Declarations
                                </a>
                            </li>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:if test="//Bibliography">
                        <li role="menuitem">
                            <a href="#{//Bibliography/@ID}" data-event-category="Table of Contents"
                               data-track-event="click" data-event-action="Link Clicked"
                               data-event-label="{//Bibliography/Heading}">
                                <xsl:value-of select="//Bibliography/Heading"></xsl:value-of>
                            </a>
                        </li>
                    </xsl:if>
                </ul>
            </div>
        </nav>
    </xsl:template>


    <xsl:template name="oscar-copyrightMessage">
        <xsl:if test="//ArticleCopyright">
            <section class="Section1 RenderAsSection1" id="CopyrightMessage">
                <h2 class="Heading js-ToggleCollapseSection">Copyright</h2>
                <div class="js-CollapseSection">
                    <xsl:apply-templates select="//ArticleCopyright"></xsl:apply-templates>
                </div>
            </section>
        </xsl:if>
    </xsl:template>

    <xsl:template
            match="Heading[parent::Bibliography or parent::Section1 or parent::Abstract or parent::AbbreviationGroup]">
        <h2 class="Heading js-ToggleCollapseSection">
            <xsl:call-template name="Heading1"/>
        </h2>
    </xsl:template>

    <xsl:template
            match="Heading[parent::Section2 or parent::AbstractSection or ancestor::ArticleBackmatter or ancestor::ChapterBackmatter]">
        <h3>
            <xsl:call-template name="Heading2"/>
        </h3>
    </xsl:template>

    <xsl:template match="Heading[parent::Section3]">
        <h4>
            <xsl:call-template name="Heading2"/>
        </h4>
    </xsl:template>

    <xsl:template match="Heading[parent::Section4]">
        <h5>
            <xsl:call-template name="Heading2"/>
        </h5>
    </xsl:template>

    <xsl:template match="Heading[parent::Section5]">
        <h6>
            <xsl:call-template name="Heading2"/>
        </h6>
    </xsl:template>

    <xsl:template match="Heading">
        <h3>
            <xsl:call-template name="Heading2"/>
        </h3>
    </xsl:template>

    <xsl:template name="Heading1">
        <xsl:attribute name="class" select="'Heading js-ToggleCollapseSection'"/>
        <xsl:variable name="level">
            <xsl:choose>
                <xsl:when test="parent::*[@RenderAs!='']">
                    <xsl:value-of select="substring(parent::*/@RenderAs, string-length(parent::*/@RenderAs))"/>
                </xsl:when>
                <xsl:when test="parent::*[name()='Section1' or name()='Section2' or name()='Section3' or
					name()='Section4' or name()='Section5' or name()='Section6' or name()='Section7']">
                    <xsl:value-of select="substring(parent::*/name(), string-length(parent::*/name()))"/>
                </xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:if test="($level > 0) and ($level &lt;= $numberingDepth) and not(ancestor::Appendix)
			and not($numberingStyle='ChapterOnly') and not($numberingStyle='Unnumbered') and not(parent::*[@RenderAs])">
            <xsl:call-template name="printNumber"/>
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template name="renderArticleRelatedObject">
        <xsl:if test="//ArticleRelatedObject">
            <xsl:choose>
                <xsl:when test="//ArticleRelatedObject/@RelatedObjectType='ErratumTo'">
                    <placeholder title="Erratum To" id="{//ArticleRelatedObject/RelatedObjectDOI}"/>
                </xsl:when>
                <xsl:when test="//ArticleRelatedObject/@RelatedObjectType='ErratumFrom'">
                    <placeholder title="Erratum From" id="{//ArticleRelatedObject/RelatedObjectDOI}"/>
                </xsl:when>
                <xsl:otherwise>
                    <div class="Box Box--gamma Box--warning u-marginTopL u-marginBtmL">
                        <div class="Message">
                            <span class="IconBox IconBox--fill IconBox--warning">
                                <span class="Icon Icon--info-white" aria-hidden="true"></span>
                            </span>
                            <div class="Message_content">
                                <p>
                                    This article has a relationship to the following article
                                    <a href="../{//ArticleRelatedObject/RelatedObjectDOI}">
                                        <xsl:value-of select="//ArticleRelatedObject/RelatedObjectDOI"/>
                                    </a>
                                </p>
                                <p>
                                    If this is incorrect please contact our
                                    <a href="/about/contacts">customer services team</a>
                                </p>
                            </div>
                        </div>
                    </div>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
