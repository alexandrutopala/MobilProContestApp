package ro.infotop.journeytoself.repository;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.newsModel.Article;
import ro.infotop.journeytoself.model.newsModel.News;
import ro.infotop.journeytoself.model.newsModel.Source;
import ro.infotop.journeytoself.service.IllnessesController;
import ro.infotop.journeytoself.service.ResourceController;
import ro.infotop.journeytoself.utils.DateUtils;
import ro.infotop.journeytoself.utils.MatchingManager;

public class NewsRepo extends DelayedRepository{
    private static long idGenerator = 0;
    private static Map<Date, Article> mockArticleDb;
    private static Map<Long, String> mockArticlesContent;

    static {
        initMockDb();
        initContents();
    }

    public NewsRepo() {
        super(false);
    }

    public NewsRepo(boolean simulateDelay) {
        super(simulateDelay);
        this.simulateDelay = simulateDelay;
    }

    public int getArticlesCount() {
        return mockArticleDb.size();
    }

    public News getArticles(Date lastUpdate, int fromIndex, int howMany, @Nullable Long matcherCode) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        Iterator<Date> it = mockArticleDb.keySet().iterator();
        int selected = 0;
        int index = 0;
        News news = new News();
        List<Article> articles = new ArrayList<>();

        while (it.hasNext() && selected < howMany) {
            if (index < fromIndex) {
                it.next();
                index++;
                continue;
            }

            Date key = it.next();
            Article art = mockArticleDb.get(key);

            if (matcherCode == null || MatchingManager.codesAreMatching(matcherCode, art.getMatcherCode())) {
                articles.add(art);
                selected++;
            }
            index++;
        }
        news.setFromIndex(index);
        news.setArticles(articles);
        news.setTotalResult(selected);
        news.setStatus("ok");
        return news;
    }

    public Article findArticleById(long id) {
        for (Article art : mockArticleDb.values()) {
            if (art.getId() == id) {
                return art;
            }
        }
        return null;
    }

    public String getArticleContent(long articleId) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);
        return mockArticlesContent.get(articleId);
    }


    private static void initMockDb() {

        // TODO: insert entries
        Comparator<Date> comp = (d1, d2) -> -d1.compareTo(d2);
        mockArticleDb = new TreeMap<>(comp);
        Article art;


        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("28.02.2019-10:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Simona Ioniță",
                        "Teama de autoritate. Vocea proprie și sentimentul de sine al copiilor",
                        "Frica este ceva ce resimțim frecvent, încă din copilărie. O experimentăm în diferite moduri în fiecare zi și dezvoltăm diferite strategii pentru a face față acestei senzații dificile. Frica, neliniștea, îndoiala, teama, panica, îngrijorarea – toate sunt derivate și conectate între ele. Învățăm de mici să ne temem. Ne temem de părinți, de profesori, de ",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Teama-de-autoritate.-Vocea-proprie-%C8%99i-sentimentul-de-sine-independent-al-copiilor-680x380.jpg",
                        "28.02.2019-10:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS, Disease.ANXIETY)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("23.02.2019-13:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Oana Calnegru",
                        "Pentru o învățare eficientă, țineți cont de curba uitării",
                        "Dacă există ceva ce mi-aş fi dorit mult să fi învăţat în şcoală este să mă fi instruit cineva în arta învăţării. Adică, cineva care să mă înveţe cum să învăţ! Îmi amintesc cum ore întregi petreceam memorând comentarii şi formule matematice, datele istorice, formele neregulate ale verbelor la engleză, simbolurile elementelor la chimie. Oricâte carioca foloseam, oricâte ore petreceam",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Pentru-o-%C3%AEnv%C4%83%C8%9Bare-eficient%C4%83-%C8%9Bine%C8%9Bi-cont-de-curba-uit%C4%83rii-680x380.jpg",
                        "23.02.2019-13:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.DEPRESSION)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("22.02.2019-10:00") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "György Gáspár",
                        "Cum recunoaștem depresia și cum îi facem față în cuplu?",
                        "„Am nevoi de ajutor, cred. De ceva timp, simt că starea mea psihică nu mai e ce-a fost. Parcă nu-mi găsesc locul și sunt tristă. Partenerul îmi spune că s-ar putea să fie depresie. Îmi dau seama că el ar vrea să mă ajute, dar nu știu ce-ar putea să facă pentru mine. Poate îmi dați câteva sugestii, dacă nu",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Cum-recunoa%C8%99tem-depresia-%C8%99i-cum-%C3%AEi-facem-fa%C8%9B%C4%83-%C3%AEn-cuplu-680x380.jpg",
                        "22.02.2019-10:00"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS, Disease.DEPRESSION, Disease.ANXIETY)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("20.02.2019-20:07") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "P. H. Madore",
                        "Când și cum putem folosi consecințele în parenting?",
                        "Consecințele reprezintă adesea partea activă din setarea limitelor și contează uneori mai mult decât limita în sine. Consecințele aplicate corect sunt cele care pot remedia un stil de parenting nepotrivit. Dacă erați adeptul permisivității, consecințele vă vor reda credibilitatea și autoritatea. Dacă erați adeptul unui stil autoritar, consecințele vor readuce în relația voastră cu copilul cooperarea bazată pe încredere și",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/C%C3%A2nd-%C8%99i-cum-putem-folosi-consecin%C8%9Bele-%C3%AEn-parenting-680x380.jpg",
                        "20.02.2019-20:07"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS, Disease.COUPLE_PROBLEMS)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("14.02.2019-10:57") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Adela Moldovan",
                        "Cum să ne certăm „înțelept“? Ce să facem și ce să nu facem într-o ceartă",
                        "Aceasta e întrebarea... „De ce ne certăm și cum facem să nu ne mai certăm?“ e adesea preocuparea partenerilor dintr-un cuplu. Sigur că nimeni nu își propune să se certe, iar momentul în care ne certăm nu e niciodată plăcut. De multe ori însă, el se dovedește util. Cearta reprezintă momentul în care iese la iveală o nemulțumire, o frustrare,",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Cum-s%C4%83-ne-cert%C4%83m-%E2%80%9E%C3%AEn%C8%9Belept%E2%80%9C-Ce-s%C4%83-facem-%C8%99i-ce-s%C4%83-nu-facem-%C3%AEntr-o-ceart%C4%83-680x380.jpg",
                        "14.02.2019-10:57"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.COUPLE_PROBLEMS)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("10.02.2019-15:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Pagina de Psihologie",
                        "Interviu cu dr. Vasi Rădulescu – O inimă îndrăgostită e culegătoare de povești",
                        "Cum ai defini sănătatea în câteva cuvinte? Să fii sănătos înseamnă să te găsești în armonie cu tine însuți și să nu ai absolut nicio boală. În timpurile curente, nu știu câți se pliază perfect pe definiția asta. Care sunt recomandările medicale corecte, în situația în care corpul nostru are o suferință? În primul rând, să știm să identificăm niște",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Interviu-cu-dr.-Vasi-R%C4%83dulescu-%E2%80%93-O-inim%C4%83-%C3%AEndr%C4%83gostit%C4%83-e-culeg%C4%83toare-de-pove%C8%99ti-680x380.jpg",
                        "10.02.2019-15:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS, Disease.COUPLE_PROBLEMS)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("03.02.2019-14:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Simona Ioniță",
                        "Borderline. La granița dintre iubire și lipsă de sens",
                        "Ea are 26 de ani. Se simte neînțeleasă, este foarte impulsivă. Atunci când se supără, devine irațională și adoptă o perspectivă de tip „alb sau negru“. Relațiile ei sunt instabile și intense. Trece rapid de la îndrăgostirea de imaginea idilică la o reacție devalorizare, atunci când simte și interpretează că celălalt nu îi oferă suficient sau nu este acolo pentru",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Borderline.-La-grani%C8%9Ba-dintre-iubire-%C8%99i-lips%C4%83-de-sens-680x380.jpg",
                        "03.02.2019-14:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.COUPLE_PROBLEMS, Disease.ANXIETY, Disease.DEPRESSION)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("29.01.2019-09:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "György Gáspár",
                        "Pentru toate mamele din lume",
                        "De curând, Mirela Retegan m-a întrebat, în cadrul unui eveniment public, ce înseamnă mama pentru mine. Recunosc că, până în acel moment, nu am stat să mă gândesc în profunzime la cum ar fi fost viața mea, dacă mama n-ar fi fost „mama mea“, ci a altcuiva. Pentru că totul îmi părea atât de firesc și de natural, cu ea",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/03/Pentru-toate-mamele-din-lume-680x380.jpg",
                        "29.01.2019-09:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("28.01.2019-13:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Sabina Strugariu",
                        "O viață măruntă. 33 de motive de recunoștință învățate până la 33 de ani",
                        "În dimineața zilei în care am împlinit 33 de ani, am simțit nevoia să mă așez în mine însămi și să stau puțin cu viața mea la o cafea. Se întâmplă totul cu așa o viteză, încât, deși co-existăm, nu prea reușim să ne simțim pe de-a-ntregul – și uneori uit să mă opresc și să mă minunez de ea",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/02/O-via%C8%9B%C4%83-m%C4%83runt%C4%83.-33-de-motive-de-recuno%C8%99tin%C8%9B%C4%83-%C3%AEnv%C4%83%C8%9Bate-p%C3%A2n%C4%83-la-33-de-ani-680x380.jpg",
                        "28.01.2019--13:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("24.01.2019-11:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Anca Pavel",
                        "Sindromul impostorului sau „acel ceva“ care ne afectează percepția sinelui",
                        "Ți se întâmplă să fii felicitat pentru realizări profesionale, iar tu să răspunzi în genul „N-am făcut mare lucru, mi-am făcut doar treaba!“? Crezi că ai obținut succesul pentru că te-ai aflat la momentul potrivit, în locul potrivit? Îți este dificil să primești complimente? Te compari des cu cei din jurul tău și ți se pare că ei sunt mai",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/02/Sindromul-impostorului-sau-%E2%80%9Eacel-ceva%E2%80%9C-care-ne-afecteaz%C4%83-percep%C8%9Bia-sinelui-680x380.jpg",
                        "24.01.2019-11:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.DEPRESSION, Disease.ANXIETY)));

        mockArticleDb.put(
                DateUtils.parseStringDateAndTime("22.01.2019-10:27") ,
                art = new Article(
                        idGenerator++,
                        new Source(null,  "https://www.paginadepsihologie.ro/articole/"),
                        "Nina Sofian",
                        "Ce moștenire relațională le lăsăm copiilor noștri?",
                        "În orice mod imaginabil, familia este legătura noastră cu trecutul şi podul către viitorul nostru. – Alex Haley Echilibrul copiilor noștri începe cu echilibrul din relația dintre mamă și tată, indiferent că formează sau nu un cuplu. O relație sănătoasă între aceștia îi ajută pe copii să se simtă iubiți și în siguranță. Este important pentru copii să petrecem timp de calitate",
                        "https://www.paginadepsihologie.ro/articole/",
                        "https://www.paginadepsihologie.ro/wp-content/uploads/2019/02/Ce-mo%C8%99tenire-rela%C8%9Bional%C4%83-le-l%C4%83s%C4%83m-copiilor-no%C8%99tri-680x380.jpg",
                        "22.01.2019-10:27"
                ));
        art.setTitle(idGenerator + ". " + art.getTitle());
        art.setMatcherCode(Disease.generateMatchingCode(Arrays.asList(Disease.FAMILY_PROBLEMS)));

        mockArticlesContent = new HashMap<>();

        String longText = ResourceController.getInstance().getResource().getString(R.string.large_text);
        for (Article a : mockArticleDb.values()) {
            mockArticlesContent.put(a.getId(), longText);
        }
    }

    private static void initContents() {
        String[] contents = {
                "Frica este ceva ce resimțim frecvent, încă din copilărie. O experimentăm în diferite moduri în fiecare zi și dezvoltăm diferite strategii pentru a face față acestei senzații dificile. Frica, neliniștea, îndoiala, teama, panica, îngrijorarea – toate sunt derivate și conectate între ele. Învățăm de mici să ne temem. Ne temem de părinți, de profesori, de tot ce înseamnă autoritate. Emoțiile pot fi atât de brute, motivele atât de diverse, iar soluțiile atât de evazive!…\n" +
                        "\n" +
                        "Din când în când, fiecare copil se confruntă cu frica. Pe măsură ce explorează lumea din jurul lui, trăind experiențe inedite și confruntându-se cu noi provocări, anxietățile aproape că reprezintă o parte inevitabilă a creșterii. Copilul are o mulțime de temeri. Copilul are teama de a fi părăsit de părinți, mai ales de mamă. Către sfârșitul primului an de viață, această frică devine tot mai puternică. Iar teama lui de străini crește. Când mama pleacă din cameră, copilul strigă. Dacă cel mic se poate mișca, încearcă să-și urmeze mama. Bebelușul are o frică naturală de a fi lăsat singur, dar și o frică de întuneric.\n" +
                        "\n" +
                        "În copilărie, suntem îndrumați să învățăm lucruri noi, să înfruntăm provocările ce ne ies în cale pentru prima oară, să ne depășim temerile și să navigăm într-o lume care nu are întotdeauna sens pentru noi. Învățăm ce e bine și ce e rău, asimilăm binele și răul, pur și simplu luăm totul ca atare de la părinți sau aparținători. Mergem pe tot parcursul vieții cu aceste idei despre bine și rău, idei care, de fapt, nu ne aparțin și pe care cu greu mai reușim să ni le schimbăm când evoluăm.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Unii părinți utilizează strategii autoritare în parenting, care nu-i permit copilului să aibă o voce proprie sau un sentiment de sine independent. Alți părinți, suprasolicitați ei înșiși, devin prea permisivi și nu îi învață pe copii despre limite și autocontrol. Cercetările arată că ambele extreme pot interfera negativ cu abilitatea copiilor de a-și regla emoțiile și de a forma relații sănătoase, ca adulți. Respectarea sentimentelor pe care le are cel mic înseamnă, printre altele, ca părinții să-i permită libertatea de a alege, bineînțeles între anumite limite clare, care să marcheze și să separe comportamentul inacceptabil de restul comportamentelor. Acesta este echilibrul sănătos la care ar trebui să aspirăm.\n" +
                        "\n" +
                        "De la părinți, învățăm să fim morali. Moralitatea nu este ceva predicat exclusiv în biserici, sinagogi sau moschei. Este o parte normală a dezvoltării umane. Învățăm moralitatea din cele mai apropiate relații – cele cu părinții noștri.\n" +
                        "\n",
                "Dacă există ceva ce mi-aş fi dorit mult să fi învăţat în şcoală este să mă fi instruit cineva în arta învăţării. Adică, cineva care să mă înveţe cum să învăţ! Îmi amintesc cum ore întregi petreceam memorând comentarii şi formule matematice, datele istorice, formele neregulate ale verbelor la engleză, simbolurile elementelor la chimie. Oricâte carioca foloseam, oricâte ore petreceam conspectând idei esenţiale din diferite texte, oricâte scheme şi oricâte exerciţii făceam – timpul petrecut cu învăţarea îmi ocupa cel mai mult din timpul aşa-zis liber, de după programul de şcoală. În anii de facultate, alăturam coloratelor carioca şi litri întregi de cafea, ce mă însoţeau prin tomurile de literatură pe care le aveam de studiat pentru examene. Aproape întotdeauna, în noaptea de dinaintea unui examen, suprindeam gânduri precum „Vai! Nu mai ţin minte nimic!“, care îmi populau mintea. Şi, chiar de nu am avut nicio restanță în toţi cei cinci ani de facultate, cu greu aş mai putea susţine acum un examen din informaţiile pritocite acum 20 de ani.\n" +
                        "\n" +
                        "Evaluarea şcolară era (şi este şi astăzi, din păcate) în cea mai mare parte despre cât de bine reuşim să exersăm funcţiile memoriei. Desigur, sunt importante şi multe alte funcţii ale creierului – cum ar fi cele de natură cognitivă, fie că vorbim de gândirea critică, strategică sau analitică, fie de imaginaţie, atenţie sau voinţă. Însă, rezultatul final evaluat este despre cât de bine reuşim să reţinem şi să reactualizăm informaţia.\n" +
                        "\n" +
                        "Dar chiar şi în afara structurii academice, a învăţării formale, zi de zi suntem supuşi provocării de a reţine tot felul de informaţii, fie că sunt unele tehnice, fie cuvinte în limbi străine, discursuri, numele oamenilor cu care interacţionăm, direcţii şi indicaţii etc.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Am fost mereu fascinată de felul în care creierul nostru, prin funcţiile lui executive, structurează informaţia astfel încât să o rețină cât mai adecvat şi, ulterior, să le-o poată transmite celorlalţi. Astăzi, îmi este evident că una dintre cele mai importante meta-abilități este aceea de a învăța-să-înveți: dacă ştiu cum să învăţ, asta mă ajută să maximizez efortul pentru învăţare, având la final un rezultat optim.\n" +
                        "\n" +
                        "Atunci când avem nevoie să fixăm ori să accesăm informaţii stocate în mintea noastră, este necesar să cunoaştem câteva aspecte legate de funcţionarea creierului, trebuie să ne înțelegem limitările cognitive și fie să găsim modalități de a ne deprinde cu acestea, fie să le folosim în avantajul nostru.\n" +
                        "\n" +
                        "Un concept important în teoriile învăţării şi în cele referitoare la funcţionarea memoriei este „efectul de spaţiere“. Pe scurt, acesta se referă la faptul că suntem mai capabili să ne reamintim informațiile și conceptele, dacă le învățăm în mai multe sesiuni, distanţate la anumite intervale de timp.\n" +
                        "\n" +
                        "Orice am învăţa (dacă ţinem cont de această strategie „de spaţiere“), vom observa – fie că lucrăm cu numere, cuvinte sau imagini, fie că ne propunem dezvoltarea de abilități sau obiceiuri – învăţarea se dovedește mai eficientă, se fixează mai repede şi este de durată. Funcţionează la orice vârstă, de la copii la vârstnici. Acest „efect de spaţiere“ poate fi folosit în orice disciplină, indiferent că ne propunem să deprindem o anumită artă sau că este vorba de ecuații matematice. Informaţiile pe care le reţinem prin această strategie pot fi asimilate pentru tot restul vieţii.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Curba uitării\n" +
                        "„Efectul spaţierii“ a fost descoperit de Hermann Ebbinghaus, un psiholog german şi un pionier al cercetării memoriei. Cele mai importante descoperiri ale sale s-au încadrat în aria uitării şi a curbei de învăţare. Mai jos este o reprezentare grafică a procesului de învăţare şi de uitare. Curba de uitare (cu roşu) indică felul cum reţinerea unei noi informaţii se descompune în creier, având cea mai accentuată „cădere“ după 20 de minute de la memorare şi ajungând să se stabilizeze la un nivel destul de constant după o zi.\n" +
                        "\n" +
                        "Putem să încetinim procesul uitării, dar pentru acest lucru este necesar să repetăm informaţia pe care am reţinut-o iniţial. Să trecem din nou şi din nou peste informaţia respectivă, la anumite intervale de timp, ne ajută să ne reamintim un procent mult mai mare din tot ceea ce avem nevoie să reţinem. Însă, aşa cum vom vedea, este important intervalul de timp la care revenim asupra informaţiei.",
                "„Am nevoi de ajutor, cred. De ceva timp, simt că starea mea psihică nu mai e ce-a fost. Parcă nu-mi găsesc locul și sunt tristă. Partenerul îmi spune că s-ar putea să fie depresie. Îmi dau seama că el ar vrea să mă ajute, dar nu știu ce-ar putea să facă pentru mine. Poate îmi dați câteva sugestii, dacă nu cer prea mult.“\n" +
                        "\n" +
                        "Acesta este una dintre cele mai recente solicitări venite pe adresa de e-mail a redacției noastre (contact@paginadepsihologie.ro). Într-adevăr, una dintre fațetele complicate ale depresiei este ilustrată și de instalarea insidioasă a stării de tristețe. Cei mai mulți dintre noi au tendința de-a pune schimbările de stare afectivă pe seama unor evenimente generale, care fac parte din viața noastră și care ar putea să reprezinte cu adevărat una dintre cauzele depresiei. Problema majoră este aceea că nu-i suficient să recunoaștem aceste cauze; adesea, avem nevoie să și facem ceva în acest sens, pentru a nu permite ca starea negativă să-și facă cuib în sufletul nostru. Exact așa cum reiese și din e-mailul primit, cei din jurul nostru – mai ales persoanele care ne cunosc suficient de bine – ar putea să citească mai corect decât noi că ceva s-a schimbat în felul nostru de a fi, a simți sau a face diferite lucruri.\n" +
                        "\n" +
                        "Recomandarea principală este aceea de a consulta un psiholog clinician și, dacă este cazul, un psihiatru. În țara noastră, diagnosticul în cazul problemelor de sănătate mintală se pune de către medicul psihiatru – de aceea este înțelept să fim suficient de inteligenți și de înțelegători față de noi înșine și să nu ne ascundem în spatele unor mituri de mult expirate sau al unor credințe limitative, care s-ar putea să ne coste ulterior prea scump, din punct de vedere psihologic. În calitate de psiholog și psihoterapeut, enumăr mai jos câteva simptome ale depresiei – a căror cunoaștere se va dovedi utilă în procesul de autoaevaluare, pentru ca mai apoi să îndrăznim să facem acea programare la un specialist:\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Senzația de energie scăzută sau de oboseală continuă\n" +
                        "Puseuri de plâns\n" +
                        "Impresia de captivitate sau blocare într-o situație\n" +
                        "Tendința de autocritică sau de judecare cu asprime a sinelui\n" +
                        "Pierderea interesului față de o serie de lucruri care anterior făceau plăcere\n" +
                        "Revenirea perpetuă la ceva din trecut – o situație, o decizie, o experiență care nu dă pace\n" +
                        "Îngrijorări cu privire la viitor și care par a se transforma în lipsă de speranță\n" +
                        "Lipsa interesului față de sex sau alte plăceri\n" +
                        "Dorința de însingurare și retragerea din interacțiuni sociale\n" +
                        "Convingerea că propria persoană e lipsită de valoare\n" +
                        "Totul pare să implice atât de mult efort, precum și apariția gândurilor de genul „Ce rost mai are să trăiesc?“\n" +
                        "Dacă majoritatea acestor descrieri ne sună cunoscut, acesta ar putea fi un indicator foarte clar al faptului că nu mai putem amâna vizita la psihiatru sau psihoterapeut. Pe site-ul nostru, există o listă cu specialiști în sănătate mintală din mai multe orașe din țară, la care puteți apela cu mare încredere.\n" +
                        "\n" +
                        "Abordând și a doua parte a întrebării – anume, ce-ar putea face partenerul în astfel de situații – foarte pe scurt răspunsul este: „Multe“. Pentru că relația de cuplu este (sau ar trebui, în mod normal, să fie) refugiul și adăpostul nostru în momentele dificile de viață. Înainte de a arăta cum ar putea să contribuie partenerul pentru a vă ajuta, fac o scurtă notă: recomandările care urmează nu exclud vizita la specialist, dar pot să completeze și să susțină foarte eficient tratamentul psihologic și/sau medicamentos.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "În calitate de psihoterapeut relațional, am constatat în practica de zi cu zi că următoarele aspecte s-au dovedit adesea a fi, pentru un cuplu, esențiale în lupta cu depresia:\n" +
                        "\n" +
                        "1. Sănătatea mintală contează la fel de mult ca sănătatea fizică. Primul pas este acela de a vorbi în cuplu despre ceea ce s-a schimbat, despre ceea ce pare diferit și despre cât de important este să avem grijă de mintea și sufletul nostru. Majoritatea oamenilor conștienți de importanța stării de bine au un psiholog sau psihoterapeut la care apelează la nevoie. În țările cu bunăstare crescută, așa cum există medici de familie, tot așa există și psihoterapeuți de familie. Iar recomandarea mea este ca prima vizită să fie făcută împreună, chiar dacă vorbim despre o consultație individuală și nu de una pentru cuplu. Prezența partenerului poate face mai ușor de gestionat anxietatea. Iar acesta poate adresa personal întrebări și poate nota recomandările primite de la specialist.\n" +
                        "\n" +
                        "2. Tratamentul este esențial. Dacă s-a pus un diagnostic de depresie, este important să luăm lucrurile în serios și, în calitate de parteneri, să susținem respectarea tratamentului. Pacientul depresiv se descurajează frecvent și s-ar putea întâmpla să abandoneze psihoterapia înainte de a vedea primele rezultate. În astfel de situații, avem de oferit multe încurajări. Iar dacă este cazul, ar putea fi utilă schimbarea specialistului cu care lucrează jumătatea noastră – desigur cu acordul acesteia.\n" +
                        "\n" +
                        "3. Discuțiile despre cum afectează depresia relația de cuplu. Nu este chiar ușor de trăit cu depresia, nici pentru cel care a fost diagnosticat și nici pentru aparținători. Adesea partenerul cu depresie este iritat și lipsit de răbdare. După cum se spune, „îi sare țandăra repede“. În astfel de momente, este indicat să ne păstrăm maturitatea și să încercăm să înțelegem particularitatea situației. Așadar, primul pas constă în validarea stării sale emoționale: „Observ că s-a întâmplat ceva, îmi cer scuze că nu am fost mai atent!“ Iar dacă persoana afectată se liniștește, este important să continuăm dialogul. Dacă nu este momentul potrivit, putem amâna discuția pentru mai târziu – numai să n-o dăm uitării. În acest dialog, este important ca ambii parteneri să-și exprime emoțiile folosind un ton civilizat și cuvinte respectuoase.\n" +
                        "\n" +
                        "4. Siguranța fizică trebuie urmărită mereu. În unele situații, depresia vine cu gânduri sau tentative de suicid. Iar dacă sunt semne că aceste gânduri ar putea fi puse în aplicare, atunci este important ca partenerul persoanei afectate de depresie să discute cu specialistul despre pașii unei intervenții în caz de criză.\n" +
                        "\n" +
                        "5. Nevoile copiilor trebuie să rămână permanent în prim-plan. Dacă există și copii în familie, este important ca aceștia să nu aibă de suferit din cauza depresiei unui adult. E obligatoriu să nu fie ignorați. Acest lucru este valabil mai ales dacă adultul cu depresie este și părintele direct implicat în tot ceea ce înseamnă parenting. Nevoile copiilor sunt variate, iar părintele cu depresie (de cele mai multe ori) nu va putea să facă față de unul singur. De aceea, s-ar putea să fie bine-venite câteva noi aranjamente – ca, de exemplu, prezența unui alt adult care să ofere suport, în timp ce părintele cu depresie își urmează tratamentul.\n" +
                        "\n" +
                        "6. Atingerile sunt vindecătoare. Uneori, partenerului cu depresie îi vine greu să se exprime în cuvinte. Dar în mod cert are nevoie de îmbrățișări, mângâieri și alte atingeri dătătoare de siguranță și vitalitate. Totuși, sunt de evitat tentativele sexuale, mai ales dacă partenerul cu depresie spune că nu are starea potrivită. Oamenii pot rezista mult timp fără sex, dar vitalitatea lor se stinge dacă nu au parte de atingeri.",
                "Consecințele reprezintă adesea partea activă din setarea limitelor și contează uneori mai mult decât limita în sine. Consecințele aplicate corect sunt cele care pot remedia un stil de parenting nepotrivit. Dacă erați adeptul permisivității, consecințele vă vor reda credibilitatea și autoritatea. Dacă erați adeptul unui stil autoritar, consecințele vor readuce în relația voastră cu copilul cooperarea bazată pe încredere și respect reciproc, înlocuind frica și intimidarea. Dacă pendulați între cele două abordări, veți dobândi consecvență.\n" +
                        "\n" +
                        "Consecințele sunt ceea ce se întâmplă după un comportament al copilului. Există consecințe pozitive, pe care este bine să le folosim pentru a-i întări comportamentele adecvate; însă de cele mai multe ori, atunci când aplicăm consecințe, este vorba de unele negative, care nu sunt bine primite de copil și care sunt menite să pună capăt unui comportament inadecvat.\n" +
                        "\n" +
                        "Consecințele nu sunt pedepse\n" +
                        "Când am început să fac voluntariat la Centrul pentru Educație Emoțională și Comportamentală pentru Copii, am fost șocată de felul în care le auzeam pe colegele mele impunându-le copiilor limite și consecințe și educând părinții să aplice consecințe. Eroarea mea, la acea vreme, era că puneam semnul egal între „consecință“ și „pedeapsă“. Pedepsele (spre deosebire de consecințe) sunt stabilite de părinte în mod arbitrar și fac copilul să se simtă prost pentru ceea ce este, și nu pentru ceea ce a făcut. Pedepsele opresc comportamentul într-un timp foarte scurt, dar nu au nicio valoare de învățare a comportamentelor adecvate. Spre deosebire de consecințele logice, pedepsele aduc de cele mai multe ori durere fizică și umilință și reprezintă modul prin care părintele face abuz de autoritatea sa.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Caracteristicile consecințelor eficiente\n" +
                        "Aplicate cu intenția de a pedepsi ori într-o manieră inconsecventă, consecințele vor avea o valoare limitată. Însă dacă sunt aplicate imediat după comportamentul inacceptabil, în mod consecvent, în legătură logică și direct proporțional cu gravitatea acestuia, atunci vor atinge cea mai mare valoare disciplinară.\n" +
                        "\n" +
                        "Imediate: Consecințele sunt eficiente, atunci când sunt aplicate imediat după comportamentul inadecvat, deoarece copilul poate face conexiunea cauză-efect între comportament și acțiunea întreprinsă de părinte.\n" +
                        "\n" +
                        "Consecvente: Această caracteristică presupune ca, de fiecare dată când comportamentul reprobabil apare, consecința să fie aplicată. Inconsecvența poate proveni din mai multe surse: fie una zicem și alta facem; fie un părinte zice „hăis“ și altul „cea“, fie odată aplicăm și altădată nu.\n" +
                        "\n" +
                        "Logice: De câte ori nu ați auzit în parc replica „Dacă mai plângi, mergem acasă“? Nu există nicio legătură logică între plâns și mersul acasă ori în altă parte, la fel cum, dacă nu plătești factura de electricitate, nu ți se va tăia apa de la robinet, ci evident… curentul electric.\n" +
                        "\n" +
                        "Proporționale: Asta înseamnă nici prea lungi și nici prea scurte, nici prea mult, nici prea puțin, raportat la comportamentul inadecvat… Nu există o modalitate prea științifică de determinare, dar bunul-simț vă va ghida. Cel mai adesea părinții tind să exagereze și să seteze consecințe prea lungi sau prea dure. Însă îmi vine în minte exemplul unui băiat de 13 ani, ai cărui părinți îi cumpărau un telefon mobil nou de fiecare dată când cel pe care îl avea se spărgea sau se strica. În decursul a 6 luni, acest copil primise 4 telefoane noi. În acest caz, nu au existat consecințe ori au fost prea neînsemnate. O consecință logică și corectă ar fi fost ca, după spargerea primului telefon sau (cel mult) al doilea, copilul să nu mai primească un nou telefon.\n" +
                        "\n" +
                        "Un ultim aspect care trebuie menționat se referă la atitudinea părintelui după consecința impusă. Eficacitatea consecințelor este adesea redusă de faptul că părinții insistă cu prelegeri, interogatorii și altele asemenea. Însă, odată ce consecința a luat sfârșit, ar trebui să se facă tabula rasa și să intervină iertarea și repararea.\n" +
                        "\n" +
                        "Momente propice în care puteți folosi consecințele\n" +
                        "Consecințele pot fi folosite în orice fel de situații, astfel că lista de mai jos nu este una exhaustivă, ci mai degrabă un ghid – pentru a începe cu situațiile cele mai des întâlnite:\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "• Când apar conflicte între copii. Consecința logică, atunci când copii nu reușesc să găsească o modalitate de a se înțelege, este să-i separați pentru o perioadă de timp. Fetele mele au dificultăți în a împărți piesele de Lego. Indiferent cât de multe sunt, cea mică le vrea fix pe cele pe care le mânuiește sora ei mai mare, iar cea mare consideră că este proprietara de drept a pieselor și nu dorește să le împartă. După ce am confiscat de câteva ori cutia cu piese de Lego pentru tot restul după-amiezii, ele au reușit să găsească o modalitate de a colabora și a le împărți.\n" +
                        "\n" +
                        "• Când jucăriile sau lucrurile nu sunt folosite în mod adecvat. Consecința logică este confiscarea obiectului pentru o perioadă limitată de timp. De exemplu, copilul meu cel mic era foarte amuzat când dărâma pianul (de jucărie). Explicațiile pe care i le dădeam – că se poate strica și că nici nu este făcut pentru așa ceva ori că mă deranjează zgomotul pe care îl face în cădere – au rămas fără rezultat. După ce am impus consecința în câteva rânduri și am lăsat-o fără pian timp de câte două ore, ea a început să nu îl mai dărâme.\n" +
                        "\n" +
                        "• Când face dezordine sau mizerie. Consecința logică este să strângă sau să curețe. Nu mai târziu sau după ce se va mai juca, ci atunci pe loc.\n" +
                        "\n" +
                        "• Când nu își așteaptă rândul sau nu respectă regula conform căreia împărțim jucăriile cu ceilalți. Consecința logică este întreruperea temporară a activității.\n" +
                        "\n" +
                        "• Când strică jucăriile proprii sau pe-ale altora (cu atât mai mult). Consecința logică ar fi să găsească o modalitate de a repara, a înlocui sau a plăti obiectul stricat. Revenind la exemplul băiețelului de mai sus, care nu avea grijă de telefonul său, aceasta ar fi o consecință musai de impus de către părinți.\n" +
                        "\n" +
                        "• Când abuzează de anumite privilegii. Consecința logică este pierderea temporară a privilegiului sau modificarea programului. Această consecință funcționează foarte bine în privința întârzierilor sau a nerespectării programului stabilit; de exemplu, dacă întârzie de la școală sau nu respectă timpul alocat jocului cu tableta ori pe computer.\n" +
                        "\n" +
                        "• Când în mod repetat nu își face temele sau nu participă la treburile casei. Consecința logică este limitarea activităților plăcute.\n" +
                        "\n" +
                        "• Când lovește. O consecință logică este scoaterea din context, îndepărtarea de restul grupului.\n" +
                        "\n" +
                        "Nu vă așteptați ca aceste măsuri să fie primite cu bucurie. Din contră, este posibil ca reacția să fie una virulentă. Dacă veți rămâne însă fermi pe poziție, copilul va înțelege până la urmă care este responsabilitatea lui și legătura cauză-efect.",
                "nu își propune să se certe, iar momentul în care ne certăm nu e niciodată plăcut. De multe ori însă, el se dovedește util. Cearta reprezintă momentul în care iese la iveală o nemulțumire, o frustrare, o diferență între noi referitoare la modul în care gestionăm lucrurile sau o diferență cu privire la ceea ce ne dorim etc. Este o oportunitate de reglare, de re-armonizare a relației. Iar a ne abține de la ceartă (pentru că nu suportăm tensiunea emoțională a acelui moment) duce la ratarea unei asemenea oportunități. Totuși, strategia aceasta – utilizată în mod repetitiv – duce, uneori, relația într-un punct din care nu ne mai putem întoarce. Pentru că diferențele sunt prea mari și eforturile de reglare devin extenuante.\n" +
                        "\n" +
                        "Așadar… să ne certăm sau să nu ne certăm? Să ne certăm cu… înțelepciune – aș spune eu, oricât de ciudat ar suna.\n" +
                        "\n" +
                        "Ce să nu facem într-o ceartă?\n" +
                        "1. Să nu jignim. De fapt, să nu spunem decât ceea ce nu ne-ar deranja să fie difuzat la televizor, de exemplu. E un exercițiu pe care adesea îl propun cuplurilor: „Dacă la voi în casă ar exista camere ascunse, cum ar arăta cearta voastră, astfel încât să nu vă fie rușine față de cei cunoscuți, pentru ce și cum ați spus?“\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "2. Să nu țipăm. Fantastic de greu de făcut. „Yelling silences your message“, spune autoarea L.R. Knost. Dacă am reuși să ținem minte că, atunci când urlăm, ni se aude vocea, dar mesajul nostru e redus la tăcere, certurile noastre ar fi mult mai eficiente.\n" +
                        "\n" +
                        "3. Să nu dăm vina pe celălalt. În momentul în care partenerul e declarat vinovat pentru ce-a făcut, pentru ce spune, pentru că țipă, pentru că nu ascultă, în curând mintea mea îl va percepe ca pe un dușman pe care va vrea să îl înfrângă, să îl rănească. Și ajungem într-o luptă de putere, din care iese șifonată rău relația.\n" +
                        "\n" +
                        "4. Să nu căutăm o soluție imediată. Mintea tulburată de nemulțumire, frustrare, încărcată emoțional nu poate găsi soluții eficiente pentru probleme complexe de viață, cum sunt cele dintr-o relație. În astfel de momente, vom găsi doar soluții radicale, gen „ne despărțim“ sau „tu trebuie să faci exact cum spun eu“.\n" +
                        "\n" +
                        "5. Să nu readucem în discuție trecutul. Sigur că multe dintre problemele noastre sunt repetitive (studiile lui John Gottman arată că aproximativ 70% dintre problemele unui cuplu nu se rezolvă). Dar dacă în fiecare ceartă aducem mereu în discuție anumite lucruri din trecut, amplificăm dimensiunile problemelor noastre și ne micșorăm speranța că putem trece peste anumite probleme.\n" +
                        "\n" +
                        "Atunci, ce să facem într-o ceartă?\n" +
                        "1. Să vorbim ca și cum cearta noastră s-ar desfășura în fața celor mai dragi oameni, cei a căror părere despre noi contează cel mai mult. Ori ca și cum ar fi difuzată live pe Facebook sau înregistrată, pentru a fi ulterior văzută de copii. Orice scenariu funcționează, dacă vă ajută să păstrați controlul vorbelor cu ajutorul cărora vă exprimați emoțiile și gândurile. Ambalajul contează.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "2. Să ascultăm. Când fiecare doar vorbește (chiar dacă respectăm punctul 1), nu apare comunicarea, în caz că celălalt nu ascultă. Chiar dacă nu ești de acord cu ceea ce spune interlocutorul, ascultă.\n" +
                        "\n" +
                        "3. Dacă începem să ridicăm tonul, e timpul să tăcem puțin. Respiră, numără până la 10, ieși pe balcon „la o țigară“ (și dacă nu fumezi). Amână discuția, până ce emoțiile tale se diminuează. În ceartă, nu funcționează zicala „Bate fierul cât e cald“. Noi nu ne prea pricepem să ne certăm, prin urmare, dacă batem fierul cât e cald, ne ardem.\n" +
                        "\n" +
                        "4. Să ne schimbăm perspectiva de la învinovățire la curiozitate. De la „Tu faci chestia asta, care pe mine mă rănește așa“ la „Mă întreb de ce faci așa, sunt curioasă să înțeleg mai bine ce se întâmplă cu tine și cu mine în această situație“. Cooperarea funcționează net superior învinovățirii.\n" +
                        "\n" +
                        "5. Să rămânem conectați. Starea de conectare dintre parteneri duce la o mai bună rezolvare a oricărei probleme și la o acceptare mai facilă a ceva ce nu poate fi schimbat. Efectul cel mai nociv al certurilor este de-conectarea partenerilor. „Mă nemulțumește asta, dar vreau să înțeleg ce se întâmplă și să căutăm o soluție. Tu contezi pentru mine și relația noastră contează.“ Transmiterea acestui mesaj către celălalt, în orice formă, e potrivită pentru cuplul vostru, fiindcă este echivalentul steagului alb.\n" +
                        "\n" +
                        "6. Să discutăm în prezent, în termeni de „aici“ și „acum“. E mult mai util și mai relevant. Ce putem face acum și de acum încolo e mult mai important decât ce-a fost cândva.\n" +
                        "\n" +
                        "Sigur, nimic din toate acestea nu este ușor de realizat. Toate necesită o practică îndelungată, un efort individual de control al propriilor emoții și un efort comun de comunicare în condiții de „stres“. Dar putem sta liniștiți: viața de cuplu e mereu palpitantă și ne aduce multe oportunități de creștere și dezvoltare (cunoscute și sub numele de „ceartă“). Totul ține de inspirația noastră de moment, pentru a recunoaște aceste oportunități, și de efortul nostru pentru a acționa conștient în gestionarea lor.",
                "Cum ai defini sănătatea în câteva cuvinte?\n" +
                        "Să fii sănătos înseamnă să te găsești în armonie cu tine însuți și să nu ai absolut nicio boală. În timpurile curente, nu știu câți se pliază perfect pe definiția asta.\n" +
                        "\n" +
                        "Care sunt recomandările medicale corecte, în situația în care corpul nostru are o suferință?\n" +
                        "În primul rând, să știm să identificăm niște simptome și să apelăm, pe criterii clare, la medic. Să ne informăm din surse bune, din articole (ideal) scrise de medici. Să nu recurgem la tratamente fantasmagorice, care n-au nicio legătură cu medicina adevărată. Avem nevoie ca de aer de educație medicală, că de aici pornește totul.\n" +
                        "\n" +
                        "Dar atunci când sufletul nostru are o suferință?\n" +
                        "Pentru suferințele sufletului, putem apela la muzică, apropierea de alți oameni, teatru, spiritualitate în toate formele ei, lecturi lungi, ședințe de introspecție. Și să nu mai considerăm dezechilibrul un stigmat sau un subiect tabu; ar trebui să ne deschidem, să vorbim despre tulburările noastre, să mergem cu încredere la psiholog sau psihiatru.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Cum se raportează românii la propria sănătate?\n" +
                        "Observ în ultimii ani fenomene care mă înfioară. Oamenii se cam culcă pe-o ureche, uită să mai aibă grijă de ei, intră în cercuri vicioase cumplite crezând că sănătatea e oricând reversibilă, ignoră principii relativ simple. Mulți nu se mai duc la medic, pentru că n-au încredere în el sau în sistemul medical per ansamblu. Au înflorit terapeuții care vindecă orice boală peste noapte, cu tot felul de dispozitive și proceduri aflate departe de medicină și de adevărul științific. Am spus cândva că mi-ar plăcea să nu mai am pacienți, să avem prevenție primordială și primară eficiente, să știe oamenii ce să facă pentru a nu se îmbolnăvi. 80% dintre bolile cardiovasculare, neurologice, precum și diabetul de tip II sunt preventibile prin dietă echilibrată și diversificată, evitarea sedentarismului, evitarea fumatului. E dramatic să ai datele astea și să vezi că țara ta e campioană la boli cardiovasculare în Europa.\n" +
                        "\n" +
                        "Ce înseamnă educația medicală? De ce este ea importantă pentru societate?\n" +
                        "Educația medicală înseamnă să ai date suficiente despre ce se petrece în corpul tău, de ce apare o afecțiune, ce reprezintă un anumit simptom, cum să recunoști un infarct miocardic acut sau un accident vascular cerebral, cum să faci niște manevre de resuscitare, cum să te menții sănătos, cum să te porți într-un spital – și lista poate continua în acest registru. O consider extrem de importantă, pentru că așa conștientizăm felul în care trebuie să avem grijă de noi și de cei de lângă noi. Dacă o rudă face infarct și intră în stop cardiorespirator, minutele până la venirea unei ambulanțe fac diferența între viață și moarte, iar un masaj cardiac bun făcut imediat poate fi salutar. Exemplele sunt numeroase aici. Vorbim totuși de sănătate, cel mai de preț lucru al nostru (inițial, tastasem greșit „preș“ în loc de „preț“ și cred că realitatea e aproape de varianta mea eronată – trebuie să schimbăm lucrurile).\n" +
                        "\n" +
                        "Cum ai descrie relația ta cu cărțile?\n" +
                        "Am citit târziu, puțin și prost. E cel mai mare regret al meu. M-am împrietenit tare cu cărțile după 20 de ani și nu era zi să nu citesc cel puțin 50 de pagini. Acum relația e oscilatorie: când lucrez la o carte de beletristică, nu citesc decât texte științifice sau colecții plate, fără un stil anume, de teamă să nu-mi contaminez propriul stil; când nu sunt prins în scriere, citesc orice și devorez lejer 20-30 de cărți pe lună.\n" +
                        "\n" +
                        "Ce te-a determinat să devii autor de carte?\n" +
                        "A fost un experiment. Eu scriam de mult timp pe blog, pe alte proiecte, pe profilul sau paginile de Facebook. Visam să am o carte a mea, dar nici prin gând nu-mi trecea cât de greu poate fi să închegi câteva sute de pagini. Nu mi-a venit să cred, când a apărut Dragă inimă. Are un puternic caracter personal și chiar confesional, astfel că a fost și un exercițiu de curaj pentru mine, care în fond mi-a prins teribil de bine. Acum corectez a patra carte și va fi o surpriză, prinsă într-un proiect frumos în care îmi pun mari speranțe.\n" +
                        "\n" +
                        "Ce înseamnă „să trăim totul cu uimire“?\n" +
                        "Mi-a rămas întipărită adânc în minte această propoziție cu iz de aforism. Uimire la procesarea lumii înconjurătoare, cu toate minunile ei desfășurate la tot pasul. Uimire la trecerea timpului, care ne dă suficient timp să construim povești frumoase și să le ținem vii prin noi înșine, că doar ele vor conta cu adevărat, într-un sfârșit. Uimire la întâlnirea cu oamenii buni care ne ies în cale și ne fac să mergem altfel înainte. Uimire la lucrurile aparent mărunte pe care nu e indicat să le pierdem vreodată. Uimire la atenția pe care trebuie să o arătăm față de noi înșine și de cei din jurul nostru, rostogolind valuri de bine în care să plutim, umplându-ne de bucurie. Uimire chiar și la trăirile cu greu și durere și suferință, că a noastră curgere prin timp și spațiu e și despre așa ceva, iar fericirea nu e un continuum dezirabil, ci tot ce ni se întâmplă în bucata asta de-o numim „viață“. Uimire și recunoștință pentru orice.\n" +
                        "\n" +
                        "La ce ne ajută meditația sau practica mindfulness?\n" +
                        "La atingerea calmului și disiparea gândurilor în exces. La o introspecție cu iz de retrospecție, când putem face un pic de ordine prin noi. La luarea piciorului de pe accelerația asta, forțată de întreaga societate din jurul nostru. La atingerea unei armonii cu noi înșine.\n" +
                        "\n" +
                        "Care este acel comportament pe care l-ai avut în acest an și care te-a scos din zona de confort?\n" +
                        "Mi-am zis că am toate ingredientele să-mi fac propria editură și să lansez cărțile mele și ale altor autori premium. Mi-am zis că pot face un studio pentru un podcast. Mi-am zis că pot încropi un proiect care să creeze povești frumoase în jurul meu. M-am și speriat tare de toată provocarea asta, de aruncarea pe un teritoriu nou, dar cred că nu suntem pe deplin pregătiți pentru nimic în viață, noi adaptându-ne din mers. Sunt aproape gata cu totul.\n" +
                        "\n" +
                        "Cine sau ce te inspiră în activitatea ta?\n" +
                        "Mă inspiră oamenii, medicii care sunt plini de empatie și bunătate, prietenii pe care mi i-am făcut în ultimii ani, muzica de calitate, plimbările lungi pe jos, călătoriile prin alte meleaguri (oh, Saint-Malo, cu toată lumina ta!), conversațiile cu substanță.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Cum este o inimă îndrăgostită?\n" +
                        "O inimă îndrăgostită e culegătoare de povești. Cred că vede cele mai frumoase lucruri din lume, mai ales pe cele din oameni. Ea galopează, se strânge, suspină sub valurile de dopamină și serotonină, tipice în prima perioadă a unei relații, cea de foc. Nu o deranjează asta. Inima poate suferi per se în lipsa iubirii, așadar un pic de grijă aici.\n" +
                        "\n" +
                        "Trei lucruri fără de care nu-ți poți imagina viața…\n" +
                        "Iubire, scris, pian.\n" +
                        "\n" +
                        "Pentru că suntem în luna martie, care este cea mai importantă lecție de viață învățată de la mama ta?\n" +
                        "Mama mi-a zis cândva că eu sunt un om foarte bun. Nu e o lecție propriu-zisă, dar eu am crescut cu sâmburele ăsta în mine. Iar acum am o sete nebună de a face lucruri bune, de a scoate cuvinte calde din inimă, de a vorbi cu sinceritate despre orice, de a fi empatic și util.\n" +
                        "\n" +
                        "Ce piesă muzicală ai ascultat pe repeat, în ultima vreme?\n" +
                        "Am avut ceva cu The Greatest Bastard a lui Damien Rice.\n" +
                        "\n" +
                        "Dacă ar fi să-i definești iubirea unui copil, ce i-ai spune?\n" +
                        "I-aș spune că iubirea asta e cel mai bun antidot pentru timp. Timpul va curge absolut la fel, dar nu ne va mai apăsa atât de tare și-l vom umple cu povești frumoase. Apoi, va trebui să-i explic ce înseamnă termenii „antidot“ și „timp“, dar îmi voi face timp și pentru asta.\n" +
                        "\n" +
                        "Crezi în feminism?\n" +
                        "Cred în prețuirea femeii, în șanse egale, în ceea ce numim meritocrație. Deci, da, sunt feminist.\n" +
                        "\n" +
                        "Cel mai mare mit în medicina zilelor noastre este….\n" +
                        "Trasul de curent. Dă toate bolile din univers.\n" +
                        "\n" +
                        "Cu ce ai vrea să rămâi în amintirea oamenilor?\n" +
                        "Nu știu cât mai stau pe aici. Cred că unora le vor rămâne niște cărți și, dacă în ele vor găsi din când în când un refugiu aproape terapeutic și-și vor aminti de mine, eu voi fi mulțumit și voi zâmbi ascuns după alte mormane de cuvinte.",
                "Ea are 26 de ani. Se simte neînțeleasă, este foarte impulsivă. Atunci când se supără, devine irațională și adoptă o perspectivă de tip „alb sau negru“. Relațiile ei sunt instabile și intense. Trece rapid de la îndrăgostirea de imaginea idilică la o reacție devalorizare, atunci când simte și interpretează că celălalt nu îi oferă suficient sau nu este acolo pentru ea. Afirmă că nu își găsește sensul pe lume. „Nu simt nimic, nu am pentru ce să trăiesc“, „Mă tot gândesc cum să mor, nimic nu mă face fericită“, le spune ea prietenilor.\n" +
                        "\n" +
                        "În copilărie, a resimțit abuzul emoțional, neglijarea. Tatăl ei era mai mult plecat; atunci când se întoarcea la ea, îi oferea dragote și susținere, iar în perioadele în care era departe de ea, fata simțea un puternic sentiment de abandon. Avea o anxietate foarte severă la separare. Când tatăl pleca, ea țipa până când acesta se întorcea. Mama era copleșită de propriul zbucium interior și își devaloriza și ironiza fiica – în orice ocazie. Incapacitatea fetei de a se comporta compatibil cu așteptările mamei a făcut-o să experimenteze sentimente negative intense și gânduri însoțite de frica de abandon. De-a lungul timpului, instabilitatea s-a răsfrânt asupra tuturor aspectelor vieții ei, inclusiv în zona relațiilor de prietenie, astfel marcate de certuri și împăcări frecvente.\n" +
                        "\n" +
                        "Când l-a întâlnit pe el, și-a zis că a întâlnit sufletul-pereche. Uneori, ea spune că partenerul ei este „cel mai bun lucru care mi s-a întâmplat vreodată“ și îi cumpără, impulsiv, daruri generoase. În alte momente, spune că îl urăște, inclusiv strigând după el sau aruncând cu lucruri în el. Imediat după asta, e cuprinsă de regret și panică, la gândul că el o va părăsi. Înainte de a începe să se întâlnească cu partenerul său actual, ea a fost implicată în relații diverse.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "I se întâmplă să devină brusc deprimată, iritată, anxioasă ori înfuriată, din motive care nu le sunt evidente altora. Nu poate tolera singurătatea, dar nici nu poate trăi fără conflicte constante. Se teme de abandon, dar îl provoacă în mod repetat, prin solicitări și plângeri nerezonabile. Atașamentul ei puternic poate fi urmat brusc de respingere. Își vede partenerul uneori ca pe un sfânt, alteori ca pe un monstru, iar aceste roluri pot fi schimbate atunci când partenerul nu-i îndeplinește așteptările uneori imposibile.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Relațiile ei sunt caracterizate în mod obișnuit de o mulțime de conflicte, suferințe și neîncredere. De multe ori, simte dezamăgire sau chiar ură față de cei dragi. Are dificultăți în a recunoaște sentimentele altora sau a empatiza cu ceilalți. Își exprimă furia în mod deschis, uneori sub formă de agresiune fizică. Iar comportamentul său furios variază de la comentarii sarcastice până la violență fizică.\n" +
                        "\n" +
                        "Manifestăm cu toții unele dintre simptomele personalității borderline? Probabil că da.\n" +
                        "\n" +
                        "În Delirurile vieții cotidiene, Leonard Shengold susține că „în sensul descris de Freud, borderline suntem cu toții, într-o măsură mai mare sau mai mică… Mintea este o entitate dinamică, oferind o gamă schimbătoare de flexibilități și intensități.“\n" +
                        "\n" +
                        "Termenul „borderline“ a fost introdus pentru prima dată în Statele Unite, în 1938. Acesta a fost un termen folosit de psihiatri, pentru a descrie persoanele care credeau ei că se află la „frontiera“ dintre psihoză și nevroză. La vremea respectivă, persoanele cu nevroză erau considerate tratabile, în timp ce persoanele cu psihoză erau considerate netratabile.\n" +
                        "\n" +
                        "Tulburarea de personalitate borderline poate determina persoanele afectate să aibă o imagine negativă de sine, să facă alegeri riscante sau impulsive, să se angajeze în comportamente autovătămătoare și să aibă emoții intense și schimbări de dispoziție. Se pare că atât un amestec de factori de mediu, cât și activitatea creierului și gena pot conduce o astfel de persoană la dificultăți în a empatiza cu sentimentele altora și la o viață marcată de teama că va fi abandonată de cei dragi. O persoană cu tulburare de personalitate borderline își petrece majoritatea timpului îngrijorându-se că va fi abandonată de cei pe care îi iubește cel mai mult și reacționează negativ din cauza acelei îngrijorări.\n" +
                        "\n" +
                        "Cinematografia și-a îndreptat, și ea, atenția către această tulburare de personalitate. „Atracție fatală“ (1987), cu Michael Douglas și Glenn Close, este unul dintre filmele care prezintă o personalitate cu tulburare borderline. Unii specialiști consideră comportamentul personajului interpretat de Glenn Close (o femeie numită Alex Forrest) ca fiind o indicație pentru tulburarea de personalitate borderline.\n" +
                        "\n" +
                        "Fie că ești membru al familiei, fie prieten sau partener al unei persoane cu tulburare de personalitate borderline, menținerea unei relații sănătoase cu o astfel de persoană poate fi o provocare. De fapt, pot exista momente când te întrebi dacă vrei cu adevărat să menții o astfel de relație. O parte esențială a iubirii față de o persoană cu tulburare de personalitate borderline constă în conștientizarea faptului că îi poți oferi un sprijin. Dar nu o poți ajuta să se vindece decât prin căutarea unor opțiuni de tratament de specialitate, pentru a putea beneficia de o terapie adecvată.\n" +
                        "\n",
                "De curând, Mirela Retegan m-a întrebat, în cadrul unui eveniment public, ce înseamnă mama pentru mine. Recunosc că, până în acel moment, nu am stat să mă gândesc în profunzime la cum ar fi fost viața mea, dacă mama n-ar fi fost „mama mea“, ci a altcuiva. Pentru că totul îmi părea atât de firesc și de natural, cu ea alături de mine și suportându-mi toanele și manifestările – plăcute sau mai puțin plăcute. Pe moment, am răspuns ce mi-a trecut prin cap și, printre altele, m-am auzit spunând că „mama este omul care mă acceptă așa cum sunt“, că „mereu ar fi de partea mea“ și că „probabil, nu voi putea să-i mulțumesc vreodată pentru tot ce-a făcut pentru mine“. Lucruri de care sunt ferm convins, dar pe care niciodată nu am stat să le analizez în adevăratul sens al cuvântului. Și, din păcate, cred că, la fel ca mine, sunt mulți alți oameni. Uităm să le apreciem cu glas tare pe mamele noastre, care sunt – pentru noi, copiii lor – dincolo de imperfecțiunile firești ale fiecărui muritor.\n" +
                        "\n" +
                        "După ce trecem de vârsta copilăriei mijlocii și începem să ne ținem bine pe picioare, nouă, copiilor, ne este relativ la îndemână să ne judecăm mamele – cu oarecare asprime. Pentru că avem ferma convingere că ele ar trebui să cunoască (și să îndeplinească imediat) toate nevoile sufletului nostru, să ne descifreze ifosele (chiar dacă se întâmplă să fie, pe moment, bolnave) și să ne accepte necondiționat (chiar și când suntem ingrați).\n" +
                        "\n" +
                        "De fiecare dată când se naște un copil, se naște și o mamă. Iar între cei doi se creează o legătură specială, care durează toată viața și, poate, chiar dincolo de această viață. O legătură care, la fel ca oricare alta din Univers, este complicată și plină de provocări, dar fără de care viața noastră ar fi și extrem de dificilă. De ce spun asta? Pentru că, atunci când primim acest dar minunat de la Dumnezeu, Divinitate, Natură (în funcție de forța superioară în care credem), nu primim și un ghid despre cum se trăiește viața. Dar primim o mamă – un suflet vulnerabil și imperfect, care se va strădui să facă tot ce poate, pentru ca nouă, pruncilor, să ne fie bine.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Și, din păcate, cei mai mulți dintre noi ajung să conștientizeze cât de mult ajutor primesc de la mamele lor abia în momentul în care acestea îmbătrânesc, nu ne mai pot ajuta sau nu se mai află fizic printre noi. Atunci ne copleșesc regretele și realizăm că nu mai avem în fața cui exprima tot ceea ce inima noastră ar avea de spus.\n" +
                        "\n" +
                        "Îmi este imposibil să nu mă gândesc și la mamele care ajung în cabinetul meu de psihoterapie și care îndrăznesc să dea glas gândurilor și temerilor; care îmi împărtășesc câteva dintre cele mai ascunse dureri ale sufletului, despre care copiii lor probabil că nu vor afla niciodată. Una dintre aceste mame îmi povestea că, atunci când copilașul ei plânge, o cuprinde disperarea. În mintea ei, aude ambulanța. Toți știm cum reacționează organismul, când sirena îți sună în urechi. Tot ea îmi spunea și despre vocea critică din capul ei, care o mustră fără milă, repetându-i că, de vreme ce copilul plânge, înseamnă că nu este o mamă bună: „Un copil fericit nu are de ce să plângă…“ Mama aceasta se uită de jur-împrejur și i se pare că toate celelalte mame se descurcă mult mai bine, fiind ferm convinsă că ea este cea mai rea mamă din lume.\n" +
                        "\n" +
                        "Și astfel, odată cu intrarea în rolul de mamă, ocazional, femeia își pierde încrederea în sine, ajungând să nu se mai recunoască. Viața ei alunecă, încet și sigur, la vale. După asta, relația cu partenerul de cuplu se deteriorează. Și cum un necaz aduce după el un altul, din păcate, multe mame nu-și mai recuperează cariera și nu-și mai îmbunătățesc imaginea de sine. Pentru că este extrem de dificil, dacă nu chiar imposibil, să faci asta de unul singur. Ai nevoie de ajutorul celor din jur. Ceea ce s-a distrus într-o relație se poate vindeca tot printr-o relație.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Asta înseamnă că mamele au nevoie de noi – copiii lor, soții lor, familia extinsă, tribul – la fel de mult cum avem și noi nevoie de ele. Au nevoie ca cineva să le vorbească despre depresia post-partum, despre anxietatea post-partum, despre tot stresul post-partum pe care-l trăiesc din momentul în care devin mame. Desigur că fiecare femeie are posibilitatea de a alege dacă, după naștere, păstrează contactul cu lumea adulților pe care o cunoaște atât de bine sau pășește în lumea parentajului și rămâne alături de puiul ei pentru o anumită perioadă. Și indiferent că este vorba despre 6 săptămâni, 6 luni sau 2 ani, acesta ajunge să devină cel mai dificil job din viața ei. Unul în care nu are pauze, nici zile libere și nici delegații; în care nu este plătită și adesea nu i se recunosc meritele pentru efortul depus.\n" +
                        "\n" +
                        "Astfel, femeia care până mai ieri era o expertă în profesia ei devine expertă în a ține bebelușii în brațe ore întregi, a schimba scutece îmbibate cu diaree sau a păși prin cameră în așa fel încât să nu scârțâie parchetul. Dar cine să fie interesat de aceste abilități nou dobândite? Pentru că noi, bărbații, suntem prea ocupați cu treburile „serioase și importante“ ale vieții; prietenii ei sunt ocupați cu alte lucruri mai interesante – distracții efemere, însă reconfortante; iar pe Facebook este cel puțin penibil să faci o postare în care să te lauzi cu cât de repede poți întinde o culme de rufe minuscule.\n" +
                        "\n" +
                        "De aceea, cred cu tărie că fiecare mamă are nevoie să i se repete (de către un alt adult apropiat – ideal ar fi, pentru asta, partenerul) că este suficient de bună; că nicio altă ființă de pe lume nu ar putea să-i ofere copilului ei la fel de multă dragoste și siguranță și conectare; că rămâne o profesionistă în domeniul ei de activitate, chiar dacă acum a luat o pauză; că prietenele ei care încă nu au copii nici nu sunt la fel de împlinite; că e la fel de frumoasă ca mai înainte; că e iubită mai mult ca oricând. A, și încă ceva, domnilor… că e la fel de sexy!\n" +
                        "\n" +
                        "Este, de asemenea, bine să știe că noi, copiii, nu ne dorim mame perfecte. Avem însă nevoie de mame fericite și împăcate cu viețile lor. Fiindcă o mamă nefericită nu poate oferi prea multă fericire altora, chiar dacă își dorește asta din tot sufletul. Cel puțin astăzi, de 8 Martie, să le fim recunoscători tuturor mamelor din lume – pentru că, fără ele, noi nu am fi cine suntem în prezent.\n" +
                        "\n" +
                        "Mamă, de la 600 de kilometri distanță, peste toată țara, îți spun că TE IUBESC!",
                "În dimineața zilei în care am împlinit 33 de ani, am simțit nevoia să mă așez în mine însămi și să stau puțin cu viața mea la o cafea. Se întâmplă totul cu așa o viteză, încât, deși co-existăm, nu prea reușim să ne simțim pe de-a-ntregul – și uneori uit să mă opresc și să mă minunez de ea și cu ea. Așa mi-a venit și ideea acestui articol, pe care îl scriu la fix o lună după prima invitație oficială la cafea. Aș fi vrut să îl scriu mai devreme, însă, de fiecare dată când mă așezam în fața colii albe, mă încerca o senzație ciudată, că aș scrie ceva prea banal sau prea superficial sau poate prea romanțios și exagerat. Am aceeași senzație și acum, însă mi-am dat seama că pot trăi cu asta. În fond, este ceea ce este.\n" +
                        "\n" +
                        "Așadar, am să împărtășesc cu voi câteva dintre lucrurile pe care le-am învățat eu în 33 de ani – nu am să le spun lecții, ci motive de recunoștință:\n" +
                        "\n" +
                        "1. „Ce bine că ești, ce minune că sunt“ – miracolul existenței mă surprinde continuu. Cu cât mă las purtată tot mai mult de complexitatea psihicului uman, cu atât îmbrățișez mai profund și cu uimire infinitul potențialului uman.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "2. Teama de necunoscut poate fi domolită (nu eradicată complet) prin explorarea necunoscutului cu curiozitate și nu cu judecată. Sună simplu, dar nu este – e nevoie de o constantă practică, de răbdare față de propriul ritm și proces, precum și de accesarea credinței că lucrurile necunoscute nu sunt doar periculoase, ci și lucruri pe care am dori să le cunoaștem.\n" +
                        "\n" +
                        "3. Familia este pentru totdeauna – indiferent cum este și cum ajungem noi să ne dezvoltăm în sânul și în afara ei și indiferent ce alegem noi să facem cu viața și cu sinele nostru, familia rămâne pământul care ne-a oferit rădăcini.\n" +
                        "\n" +
                        "4. „Nu ți–e scris în gene“ – doar pentru că familia este pentru totdeauna, nu înseamnă că suntem predestinați să ducem cu noi și să transmitem și următoarelor generații tiparele disfuncționale pe care le-am introiectat la un moment dat. Devenind conștienți și încercând noi moduri de a gândi, simți și relaționa, putem schimba lucrurile – uneori, mai puțin decât ne-am dori, însă de multe ori, mai mult decât ne-am aștepta.\n" +
                        "\n" +
                        "5. Există o infinitate de lucruri pe care le putem investi cu sens. Printre cele mai importante pentru mine, astăzi, sunt prietenia și contactul autentic cu „cealaltă familia a mea“, prietenii. Oameni frumoși și complecși, cu care pot trăi și împărtăși, care mă influențează și se lasă influențați de mine, care mă susțin când am nevoie și mă oglindesc când nu vreau să-mi văd și părțile de umbră și urât. Pentru asta, vă mulțumesc.\n" +
                        "\n" +
                        "6. Am avut nevoie de mai bine de 30 de ani, ca să învăț că iubirea de sine poate însemna mult mai mult decât egoism și autosuficiență; că a avea grijă de tine însuți este, de fapt, cea mai strâmtă și sigură cale de a învăța să ai grijă de ceilalți.\n" +
                        "\n" +
                        "7. „Într-o lume în care poți fi orice, fii bun!“ Blândețea – nu cred că există vreo altă însușire pe care să o caut, să o iubesc și să o valorizez mai mult la oameni și pe care să vreau să o îngrijesc și să o dezvolt mai mult la propria-mi persoană. Este, cred, lecția pe care o învăț cel mai greu – nevoia mea de siguranță, de dreptate și de a ști uită, uneori, cât de finite pot fi toate aceste lucruri și cât de infinită e bunătatea.\n" +
                        "\n" +
                        "8. Carțile – cred că, pentru fiecare dintre noi, există cel puțin o cheie spre introspecție, reflexivitate și cunoaștere a lumii interioare și exterioare. Eu am cărțile, toate aceste vieți pe care le trăiesc și le explorez, trăind o singură dată.\n" +
                        "\n" +
                        "9. „Cine pleacă într-o călătorie, nu se mai întoarce niciodată același“ (proverb chinezesc) – călătoriile sunt despre explorare, nu doar despre locuri, vacanțe sau bani, ci despre cunoaștere și trăire, oameni, culturi, obiceiuri și povești. Noi perspective, diferite modalități de a privi același lucru, flexibilitate și devenire.\n" +
                        "\n" +
                        "10. Mai puțin înseamnă mai mult – simplitatea este, în mintea mea, similară cu liniștea și pacea interioară. Simplu nu înseamnă ușor, așa cum „mai puțin“ nu înseamnă neapărat mai sărac sau simplist, ci mai important, esențial, autentic. Mă face acest lucru fericit? Îmi e de folos? Mă dezvoltă? Dacă nu, ce rol are în viața mea?\n" +
                        "\n" +
                        "11. Bunica mea spunea mereu că „banii sunt ochiul dracului“ – am crezut acest lucru mult timp și am preluat acest tipar de raportare la bani pentru multă vreme, însă mi-am dat seama că nu banii sunt sursa tuturor relelor din lume, ci frica și tot ce facem pentru a o ascunde, masca, acoperi.\n" +
                        "\n" +
                        "12. Viața petrecută în natură și alături de animale – cele mai importante lecții pe care le învăț despre a trăi și a relaționa le învăț din coabitarea cu două pisici, Kafka și Murasaki, cu un cățel, Jung, și cu plantele care le supraviețuiesc. Am învățat despre grijă și limite, despre frică și agresivitate, despre joc, conexiune și empatie; și, mai presus de orice, despre a trăi în prezent, cu ceea ce este.\n" +
                        "\n" +
                        "13. Fericirea nu este despre eternitate, ci despre acceptare – despre a învăța să trăim cu noi înșine și cu cei din jur în lumea asta, care uneori pare prea mică, iar alteori prea mare. Acceptarea este, astăzi, un termen interschimbabil cu fericire, în mintea mea, pentru că nu este despre a avea tot ceea ce vreau când vreau, despre a simți tot ceea ce vreau să simt, desprea a avea doar rezultatele pe care le aștept pentru a fi fericită, ci despre întreaga complexitate a experienței umane.\n" +
                        "\n" +
                        "14. Iubirea este un verb, nu un substantiv – am început să văd și să trăiesc iubirea ca pe ceva viu, mereu în schimbare și transformare.\n" +
                        "\n" +
                        "15. Am învățat să mă rog într–un mod diferit – pot să cer lucruri sau pot să cer resurse să ajung la ceea ce îmi doresc; pot să cer să nu se întâmple lucruri sau resurse ca să mă adaptez, să învăț, să conțin, să depășesc; pot să cer să dispară tot ce nu îmi place și mă doare sau pot să cer resurse ca să accept și să cresc.\n" +
                        "\n" +
                        "16. Răbdarea este, într-adevăr, o superputere!\n" +
                        "\n" +
                        "17. Joaca, râsul, dansul, veselia sunt resurse neprețuite, cărora noi, în calitate de oameni mari, serioși și responsabili, nu le mai acordăm importanță, nu le mai acordăm prioritate în cursa contra cronometru prin viață. Mi-am amintit de curând cum e să râzi cu gura până la urechi și să simți asta în fiecare celulă a corpului – mi-a dat energie pentru lucrurile care mă împovărau. Și când mă gândesc că a fost o vreme când credeam și eu că nu poți să fii jucăuș și șotios, dacă vrei să fii considerat un adult responsabil… prostii.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "18. Corpul este viață, mișcare, energie, ritm – am învățat, în sfârșit, să îmi ascult corpul. Nu îmi iese întotdeauna, dar când îmi iese, se simte și se vede. A pune corpul în mișcare, a-i da voie să exploreze, să-și descopere limitele, zonele de confort, ritmul de a face lucruri și de a se odihni – este ceva minunat! Orice formă de mișcare (activitatea fizică organizată, plimbări, dans, etc.) toate sunt indispensabile unui corp viu și sănătos. Pentru că… ce folos că ai o casă cu o mie de etaje, băi sau camere ori că arată ca în ultimul număr dintr-o revistă de design interior, dacă nu a fost îngrijită, curățată, aerisită de ani și ani?\n" +
                        "\n" +
                        "19. 0,001 este mai mult decât 0 – avem nevoie de lucruri spre care să tindem. Însă, uneori, fixându-ne obiective îndepărtate și mărețe, ne pierdem în așteptări, credințe și neîmpliniri și pierdem din vedere micile realități. Un pas nu face cât o sută, sigur; dar face mai mult decât niciunul. În fond, ce sunt o sută de pași, dacă nu un pas și apoi încă un pas și încă un pas?\n" +
                        "\n" +
                        "20. Există o diferență între a judeca și a discerne – care e dată de flexibilitatea poziției pe care o alegem. Suntem animale raționale și, dincolo de ceea ce simțim și trăim, avem capacitatea de a gândi și de a comunica prin limbaj. Cu plusuri și minusuri. Ar fi păcat, totuși, să nu ne folosim capacitatea de a discerne, atunci când facem alegeri pentru viața noastră. „Cine sunt?“; „Ce abilități, defecte, limite, resurse am?“; „Cu cine îmi place să-mi petrec timpul?“ etc. Însă a judeca presupune a alege (fie chiar și numai în mintea noastră) pentru celălalt. Cine aruncă primul piatra ar putea fi mai degrabă cel care își asumă primul răspunderea unei alegeri pentru viața altcuiva. Până la urmă, chiar este loc sub soare pentru toată lumea și întotdeauna se poate și altfel. (Dacă nu mă crezi, încearcă să-ți amintești un lucru, oricât de banal sau de important, pe care ai jurat că nu o să-l spui, crezi sau faci niciodată. Ai ajuns să-ți „înghiți“ cuvintele?)\n" +
                        "\n" +
                        "21. E atât de important ca, din când în când, să ne oprim ca să respirăm – cu tot ce presupune asta: conștientizare, observare, alegere, repornire. Atunci când ne oprim să trecem în revistă mersul lucrurilor în propria viață, ne dăm șansa de a opri un mecanism greșit, de a schimba o piesă ori două sau chiar de a schimba cu totul mecanismul. Respiră și vezi unde te afli! Respiră și vezi ce lipsește! Respiră și vezi ce e suficient! Respiră, respiră, respiră!\n" +
                        "\n" +
                        "22. Ar fi de mare ajutor să nu credem mereu tot ceea ce gândim și nici să nu presupunem că orice emoție, oricât de intensă, va dura la nesfârșit – nu prea avem multe certitudini, însă schimbare este, cu siguranță, una dintre ele. Nimic nu rămâne la fel pentru totdeauna. Lucrururile se schimbă, se transformă. Noi, oamenii, ne dezvoltăm, adaptăm, flexibilizăm sau… dimpotrivă. Complexitatea psihismului nostru este dată tocmai de această capacitate extraordinară de a simți o gamă variată de emoții, de a ne lăsa influențați de gândirea și acțiunile celor din jur și viceversa.\n" +
                        "\n" +
                        "23. Există cel puțin o soluție pentru orice problemă – am descoperit că, până și în cele mai deznădăjduite situații, lucrurile s-au așezat cumva. După cum bine spune cea mai bună prietenă a mea, „Sabs, n-a fost niciodată să nu fie cumva!“ Și cât adevăr este în această expresie! Cât m-am luptat cu asta, câte lupte interioare și conflicte exterioare nu am dus pentru o anumită soluție, pentru acel Adevăr Unic (al dreptății mele, bineînțeles), câtă zbatere, când, de fapt, plutirea este și ea posibilă, nu însă și ușor de acceptat. Atunci când ne simțim fără soluții, haide măcar să lăsăm în planul conștiinței soluțiile posibile, dar indezirabile din motivele x sau y. Există cel puțin o soluție…\n" +
                        "\n" +
                        "24. Rușinea se hrănește cu secrete – dintre toate emoțiile, cu ea m-am luptat cel mai mult. O bună bucată de timp am făcut doar ce mi-a spus, de frică să nu o dezamăgesc și să mă deconspire. Nu știam că o hrănesc, de fapt, și îi dau puterea mea. Am învățat că relațiile în care ne dăm voie să fim noi înșine sunt cel mai bun antidot.\n" +
                        "\n" +
                        "25. Avem nevoie să ne dăm voie să avem încredere, chiar dacă asta implică vulnerabilitate și risc – a avea încredere nu înseamnă a avea siguranța că nu vom fi răniți, că nu vom fi dezamăgiți sau că nu vom suferi. A avea încredere înseamnă cu adevărat că, dincolo de rană și durere, există emoții pe care merită să le trăim, pentru care merită să riscăm și fără de care am putea supraviețui, dar nu am trăi pe deplin.\n" +
                        "\n" +
                        "26. Suntem ceea ce facem în mod repetat – suntem iubire și greșeală și grijă și răbdare și încrâncenare și compasiune și bunăvoință și singurătate și reziliență și pasiune și gelozie și frică și curiozitate și fericire ș.a.m.d. Dar, cel mai mult, suntem ceea ce alegem cel mai des. Pentru mine, în sfârșit, viața nu mai este despre visuri de perfecțiune, ci despre pași mici și repetați de a alege ceea ce mă face să mă simt bine, cât mai des posibil.\n" +
                        "\n" +
                        "27. A fost o vreme când aș fi dat orice și aș fi făcut orice să mă placă toată lumea. Acum, sunt mai reținută cu ceea ce dau sau fac, pentru că, în primul rând, vreau să continui să mă plac pe mine – și nici măcar nu e vorba despre dreptate sau despre a face lucrurile bine sau despre a avea succes. Mai degrabă, este despre a mă simți bine în mine însămi. Încerc să văd dacă pot să îmi ofer ceea ce așteptam de la ceilalți. Și întâmplarea face că nici măcar eu nu mă plac întotdeauna și nu sunt mulțumită de mine. Dar, hei, unde altundeva aș putea să mă duc, nu-i așa? Atunci, aleg să dezvolt o relație de durată cu mine însămi – mi-ar plăcea să îmbătrânim frumos împreună; altfel, vom risca să ne amărâm zilele cu regrete, reproșuri și neputințe.\n" +
                        "\n" +
                        "28. Lucrez cu mine la a-mi flexibiliza gândirea, la a-mi dezvolta toleranța și la a-mi exprima, cât mai des, compasiunea și empatia. Însă încerc să-mi revizuiesc constant valorile – aveam tendința să mă agăț de câte o valoare până la identificare, iar dacă intervenea ceva care s-o modifice, o aruncam cât mai departe de mine. În ultimii ani, am început să văd valorile drept repere oarecum fluide. Am nevoie de ele, construiesc pe baza lor, văd lumea prin prisma lor, atât timp cât îmi este de folos și are sens; altfel, nu le mai arunc, dar aduc altele pe lângă ele sau le las deoparte, cu grijă, pentru când și dacă vor mai redeveni importante.\n" +
                        "\n" +
                        "29. Nimic nu trebuie. Nimic. Asta este lecția lecțiilor! Există consecințe pentru fiecare lucru, gând, alegere – consecințe resimțite de noi ca fiind plăcute sau nu, bune sau nu, de dorit sau nu. Însă nimic nu trebuie. Totul este despre libertate și asumare. Dacă vrei să fii liber să faci orice, ești obligat să îți asumi consecințele pentru ceea ce faci. Aleg să îmi asum responsabilitea libertății mele, așa cum o văd eu, decât să fac lucruri care trebuie, în limitele decise de alții.\n" +
                        "\n" +
                        "30. Spațiul personal a devenit, cu adevărat, important pentru mine – până nu demult, nici nu credeam că am un spațiu personal. Am crescut printre mulți adulți, cu o idee destul de restrânsă despre intimitate și spațiu. Țin minte că, în facultate și chiar după, obișnuiam să îmbrățișez pe toată lumea, să fiu prea prietenoasă, prea touchy-feely, cum îmi spunea un prieten. Mi se părea că ceilalți sunt prea rigizi, prea înțepați, prea speriați. Mi-a luat mult timp să îmi dau seama că nu știam unde încep eu și unde încep ceilalți. „Gardurile bune fac vecinii buni“ este unul dintre cele mai bune sfaturi pe care le-am primit vreodată. Sunt încă la școala de garduri… Încă învăț cum se construiesc, când se ridică și când se dau jos, cum vin porțile, cui aleg să dau cheile etc. Îmi place la școală.\n" +
                        "\n" +
                        "31. A fi singur nu înseamnă neapărat că te simți singur – am învățat să mă bucur de momentele de solitudine, să învăț să stau cu tăcerile și absențele. A fost o vreme în care nu știam ce să fac cu mine în absența altor persoane. Până la 30 de ani, am locuit mereu cu cineva, într-o formă sau alta, și am petrecut foarte puțin timp de una singură. Apoi, am descoperit că frica de singurătate este, uneori, chiar mai rea decât singurătatea. Uneori. Nu aduc un elogiu singurătății, nu o prefer contactului uman, dar nici nu o mai transform într-un monstru fără chip, pentru că, cel mai adesea, are chipul meu.\n" +
                        "\n" +
                        "32. A avea nevoie de ajutor și a-l cere la momentul potrivit este despre risc, vulnerabilitate, asumare și, mai presus de orice, despre această programare genetică a creierului nostru de a căuta, dezvolta și hrăni conexiuni cu cei din jur. Este frumos și bine să putem face lucruri de unii singuri, să ne descoperim resursele și capacitățile, dar este la fel de frumos și să ne putem susține unii pe ceilalți.\n" +
                        "\n" +
                        "33. Povestea vieții mele este mai mult decât povestea pe care mi-o spun mie însămi, mai mult decât poveștile pe care le spun ceilalți despre mine, mai mult decât încadrarea într-un gen literar, mai mult decât tehnica povestitorului sau punctele culminante. Îmi place tot ce știu despre ea până în acest moment și sunt curioasă privind modul în care se va desfășura acțiunea pe mai departe. Sunt recunoscătoare pentru fiecare capitol, obstacol și ajutor, pentru fiecare personaj și pentru impactul fiecăruia în parte.\n" +
                        "\n" +
                        "Gândindu-mă cum să închei acest articol, căutam pierdută cu privirea spre fereastră o fărâmă de înțelepciune, când mi-au căzut ochii pe orhideea din cabinet – uscată de o jumătate de an și având acum câțiva mugurași. Nu-i observasem înainte, dar în câteva zile vor înflori și asta mă bucură, ceea ce vă doresc și vouă! Bucurie!",
                "Ți se întâmplă să fii felicitat pentru realizări profesionale, iar tu să răspunzi în genul „N-am făcut mare lucru, mi-am făcut doar treaba!“? Crezi că ai obținut succesul pentru că te-ai aflat la momentul potrivit, în locul potrivit? Îți este dificil să primești complimente? Te compari des cu cei din jurul tău și ți se pare că ei sunt mai deștepți decât tine? Ești rareori mulțumit de cum îți faci treaba și consideri că e departe de a fi perfectă? Ai o teamă constantă că ceilalți vor descoperi la un moment dat că nu ești chiar atât de capabil pe cât cred ei?\n" +
                        "\n" +
                        "În cazul în care ți s-a părut firesc să răspunzi cu „DA“ la aceste întrebări, e momentul să iei în calcul faptul că te numeri printre cei care manifestă sindromul impostorului. Termenul nu sună deloc măgulitor, însă gașca e mare și selectă. Cuprinde și nume sonore, oameni la care nu te-ai fi gândit că s-ar mai putea îndoi de calitățile și capacitățile lor, după ce au beneficiat de un nivel înalt de recunoaștere publică.\n" +
                        "\n" +
                        "Michelle Obama, una dintre cele mai apreciate femei ale zilelor noastre, a spus, în turul pentru promovarea cărții autobiografice Povestea mea, că încă se mai simte precum un impostor și că, probabil, acest sentiment nu o va părăsi niciodată definitiv.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Umanista și mult îndrăgita Maya Angelou a rostit un gând cel puțin surprinzător pentru cei care o apreciază: „Am scris 11 cărți, dar de fiecare dată mă gândesc: «Ah, de data asta vor afla! I-am păcălit pe toți, dar acum sunt pe cale să fiu descoperită.»“\n" +
                        "\n" +
                        "Michelle Pfeiffer, Kate Winslet, Emma Watson, Tom Hanks, Seth Godin… sunt doar câteva alte nume celebre despre care ai putea spune orice, în afară de faptul că nu își merită realizările. Însă, cu toate confirmările și reconfirmările, ei continuă să se simtă ca niște impostori, care beneficiază de aplauze și admirație fără să le merite cu adevărat. Poate că sună a modestie, dar nu vă lăsați păcăliți de această interpretare.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Mulți dintre oamenii de succes împărtășesc un secret: în profunzime se simt ca niște escroci și au convingerea că realizările lor se datorează, pur și simplu, unui mare noroc. În ciuda faptului că nu există dovezi care să susțină o astfel de credință, ei se simt deseori copleșiți de sentimente de nesiguranță sau îndoială de sine. Pot chiar să nu considere că nivelul pe care l-au atins se încadrează la „succes“, pentru că și-au programat setul de standarde astfel încât să valideze doar realizările ieșite din comun ale celorlalți.\n" +
                        "\n" +
                        "Sindromul impostorului nu are o încadrare clinică, ci este un tipar psihologic prin care un individ se îndoiește de propriile realizări și trăiește cu o intensă teamă că va fi expus public din cauză că a comis o mare fraudă. Iar dacă trăiești în papucii unui asemenea „impostor“, este de așteptat să te confrunți cu o neputință de a-ți asuma inteligența, abilitățile, munca și rezultatele obținute. În funcție de intensitatea cu care se manifestă această experiență, există un risc corelat – acela de a fi acompaniată de anxietate, stres, depresie, predispoziție pentru dependența de alcool, tutun, droguri sau alte comportamente de risc.\n" +
                        "\n" +
                        "Sindromul impostorului poate fi devastator prin orgia de senzații agonizante pe care o stârnește în interior. Gândurile și sentimentele pe care le trăiește un asemenea individ sunt în totală opoziție cu părerea oamenilor din jur, motiv pentru care există mari șanse ca nimeni să nu înțeleagă și nici să nu valideze asemenea trăiri. Absolut firesc, pentru că, până nu afli că este un fenomen studiat și destul de larg răspândit, e greu de găsit o logică în faptul că un om minunat și un profesionist remarcabil chiar ar putea crede că nu merită nici iubire, nici apreciere.\n" +
                        "\n" +
                        "„Nu sunt suficient de bun!“ este, cu toate acestea, o credință centrală a impostorului. Una care îl poate ține departe de bucuria succesului, dar și de o relație de cuplu împlinită. Deoarece sindromul afectează percepția sinelui, simptomele lui se extind în toate ariile vieții. Pentru a nu risca să fie dat în vileag ca escroc, ar putea prefera un partener sau o parteneră care să nu-l provoace intelectual sau emoțional, micșorându-și astfel riscul de abandon (presupus ca fiind iminent).\n" +
                        "\n" +
                        "În realitate, este foarte probabil ca cei care suferă de sindromul impostorului să nu aibă motive reale de a se teme de acuzația de escrocherie. Poate fi vorba de persoane pe care te poți baza oricând, pentru că preferă să muncească mai mult, să se documenteze mai bine, să țină cont de toate detaliile necesare, pentru a se asigura că excelează în tot ceea ce fac. Specialiștii consideră chiar că prezența sindromului impostorului este un semn de măreție (dar nu o condiție necesară!).\n" +
                        "\n" +
                        "Pauline Rose Clance este cea care (alături de Suzanne Imes) a studiat pentru prima oară prevalența acestei experiențe interne. Cartea ei, The Impostor Phenomenon, este o colecție impresionantă de povești ale unor oameni afectați de senzația de impostor, pe care i-a ajutat să își schimbe credințele și percepțiile, astfel încât să își poată gestiona frica ce le bântuia succesul. Într-o asemenea situație, un psihoterapeut informat se poate dovedi un ajutor de nădejde.\n" +
                        "\n" +
                        "Pentru a determina dacă ai sau nu caracteristici de impostor și, dacă da, în ce măsură ești afectat, poți accesa aici testul Clance Impostor Phenomenon Scale (CIP).",
                "Pentru că suntem în luna în care la nivel internațional se celebrează iubirea și pentru că azi este Ziua Îndrăgostiților, iată în cele ce urmează 14 idei esențiale pentru fiecare cuplu, de la experta în relații și iubire Esther Perel.\n" +
                        "\n" +
                        "Cum am putea defini iubirea?\n" +
                        "La origine, iubirea e un verb: din „a iubi“ s-a format „iubirea“. E un angajament activ față de tot felul de sentimente – și cele pozitive și cele primitive și cele detestabile. Dar e un verb la diateza activă. Și adesea e surprinzător cum poate, într-un fel sau altul, să se retragă și să se reverse înapoi ca o maree. Sau ca luna: tocmai când credeam că a intrat în eclipsă, deodată o vedem că iese dintre nori. Iubirea nu e o stare de entuziasm perpetuu.\n" +
                        "\n" +
                        "Care este arhitectura psihologică a iubirii?\n" +
                        "Iubirea se sprijină pe doi stâlpi: capitulare și autonomie. Nevoia noastră de a fi împreună cu cineva coexistă cu nevoia noastră de a trăi separat.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Ce ne poți spune despre „alesul“ sau „aleasa“ inimii?\n" +
                        "Nu există așa ceva. Există cineva pe care tu îl alegi și împreună cu care decizi că vrei să construiești ceva. Dar, în opinia mea, ar fi putut și alții. Nu există o persoană unică în lumea aceasta, care să-ți fie sortită. Contează pe cine alegi tu și ce anume vrei să clădești alături de acea persoană.\n" +
                        "\n" +
                        "Care este cheia unei relații fericite?\n" +
                        "Să asculți. Doar atât. Nu trebuie neapărat să fii de acord. Ajunge să vezi dacă poți înțelege pe altcineva, care are o perspectivă complet diferită asupra aceleiași realități.\n" +
                        "\n" +
                        "Ce facem cu certurile?\n" +
                        "E natural ca oamenii să discute de pe poziții diferite. În cazul partenerilor de cuplu, face parte din intimitatea la care au ajuns. Dar, pentru asta, trebuie să ai o bună trusă de reparații. E necesar să te poți întoarce dacă ți-ai pierdut busola – ceea ce se mai întâmplă – și să poți spune: „Am căzut în vechile mele capcane, îmi pare rău“ sau „Știi ce, mi-am dat seama că n-am auzit niciun cuvânt din tot ce-ai spus, fiindcă eram foarte supărat, așa că… Putem vorbi din nou despre asta?“\n" +
                        "\n" +
                        "Ce facem cu erotismul?\n" +
                        "Am lucrat cu atâtea cupluri care și-au îmbunătățit dramatic relația în bucătărie și n-au făcut nimic în dormitor… Însă, dacă repari viața sexuală, relația se transformă.\n" +
                        "\n" +
                        "Și dacă apare infidelitatea?\n" +
                        "Una dintre cele mai mari descoperiri și surprize pe care le-am avut făcând cercetări pentru cartea Regândirea infidelității a fost aceea că oamenii veneau și spuneau: „Îmi iubesc partenerul (sau partenera). Și am o aventură.“ Uneori, chiar dacă au o relație satisfăcătoare, oamenii calcă strâmb. Și nu înșală fiindcă își resping relația sau fiindcă reacționează negativ față de relația pe care o au. Ei adesea înșală nu pentru că vor să-și găsească pe altcineva, ci pentru că vor să-și regăsească alte versiuni ale sinelui. Nu e vorba atât de mult de faptul că vor să părăsească persoana cu care sunt, ci mai mult de faptul că uneori vor să scape de ceea ce au devenit ei înșiși.\n" +
                        "\n" +
                        "Cum e cu masculinitatea sănătoasă?\n" +
                        "Bărbații puternici sexual nu hărțuiesc, ei seduc. Bărbații nesiguri sunt aceia care au nevoie să facă uz de forță pentru a-și potoli insecuritatea și pentru a învinge inaccesibilitatea sau indisponibilitatea femeilor. Femeilor le este teamă de viol, iar bărbaților, de umilință.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Ce ne poți spune despre vulnerabilitatea masculină?\n" +
                        "N-am fost niciodată de părere că bărbații nu vorbesc. Bărbații nu sunt în stare să vorbească despre suferințele lor. Adică au un alt mod de a trece prin situații adverse. Uneori, au nevoie de mai mult timp, așa că trebuie doar să taci și să aștepți – stai deoparte. Și dacă nu întrerupi procesul, va apărea și rezultatul.\n" +
                        "\n" +
                        "Cum păstrăm vie pasiunea de-a lungul timpului?\n" +
                        "Într-o relație în care partenerii și-au luat angajamente, dorința susținută se bazează pe reconcilierea a două nevoi umane fundamentale. Pe de o parte, nevoia de securitate, predictibilitate, siguranță, încredere, certitudine și stabilitate. Pe de altă parte, nevoia de aventură, noutate, mister, risc, pericol, necunoscut și imprevizibil. Ei bine, în loc să priviți această contradicție între domestic și erotic ca pe-o problemă de rezolvat, eu vă sugerez să o vedeți ca fiind un paradox care trebuie gestionat.\n" +
                        "\n" +
                        "Ce facem cu iubirea de sine?\n" +
                        "Una dintre primele căi prin care înveți să te iubești pe tine însuți este să fii iubit de alții și să-i iubești la rândul tău.\n" +
                        "\n" +
                        "Dacă ne exprimăm franchețea este de bine?\n" +
                        "Cultura noastră e una care se înclină în fața etosului francheții absolute și promovează sus și tare adevărul până la perfecțiunea morală. Alte culturi sunt de părere că, atunci când scoți totul la lumină și echivocul dispare, s-ar putea ca asta să nu-ți mărească intimitatea, ci s-o compromită iremediabil.\n" +
                        "\n" +
                        "Cum facem schimbări reale în comportamentele noastre, cum ne exersăm noile roluri?\n" +
                        "Învățăm noi roluri exact așa cum învață copiii. Nivelul 1: Imiți; te prefaci, chiar dacă încă nu simți ceea ce afișezi. Nivelul 2: Te identifici cu persoana care se poartă așa cum ai vrea tu s-o faci. Nivelul 3: Internalizezi rolul până ce devine parte din tine. Oamenii vor spune adesea: „Bine, dar ăsta nu-s eu.“ Lor le spun: „Într-o bună zi, străinul va realiza că nu mai e un străin și că a devenit localnic.“\n" +
                        "\n" +
                        "Social media sau mindfulness?\n" +
                        "Dacă orice altceva a eșuat, renunță la social media pentru câteva zile… sau săptămâni. Timpul petrecut departe te va ajuta să înțelegi că strădania de a deveni altcineva e o experiență frustrantă. În loc s-o mai continui, focalizează-te să devii cea mai bună versiune a ta și să rămâi ancorat în viața ta de aici și de acum.\n" +
                        "\n" +
                        "Esther Perel este autoarea a două cărți care s-au tradus și în limba română – Inteligența erotică și Regândirea infidelității – și a două discursuri TED care au devenit virale la nivel internațional: The Secret To Desire  In A Long-Term Relationship și Rethinking Infidelity… A Talk For Everyone Who Has Ever Loved.",
                "\n" +
                        "Ce moștenire relațională le lăsăm copiilor noștri?\n" +
                        "13-02-2019 Nina Sofian PARENTAJ 0 Comentarii\n" +
                        "Distribuie\n" +
                        "Tweet\n" +
                        "Abonează-te\n" +
                        "În orice mod imaginabil, familia este legătura noastră cu trecutul şi podul către viitorul nostru. – Alex Haley\n" +
                        "Echilibrul copiilor noștri începe cu echilibrul din relația dintre mamă și tată, indiferent că formează sau nu un cuplu. O relație sănătoasă între aceștia îi ajută pe copii să se simtă iubiți și în siguranță. Este important pentru copii să petrecem timp de calitate cu ei, să experimentăm munca în echipă, să îi validăm și să avem o comunicare bazată pe blândețe, înțelegere și respect.\n" +
                        "\n" +
                        "Avem uneori tendința să spunem că cel mic „nu observă“ sau „nu înțelege el acum“, însă efectele acțiunilor noastre sunt mai semnificative decât ne-am aștepta.\n" +
                        "\n" +
                        "Există mai multe aspecte importante în viața psihică a copilului, ce sunt puternic influențate de relația de cuplu. Să vorbim puțin despre cele mai importante dintre ele:\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Sentimentul securității\n" +
                        "Atunci când părinții sunt capabili să-și rezolve propriile conflicte și în același timp să fie receptivi și la nevoile copilului, acesta va simți că trăiește într-un mediu sigur. Acest aspect este foarte important – atât pentru ca cel mic să aibă încredere să exploreze lumea, cât și pentru dezvoltarea sa emoțională.\n" +
                        "\n" +
                        "Valorile culturale\n" +
                        "Copilul observă felul în care relaționează adulții din jur și astfel își dezvoltă primele percepții despre modul în care funcționează societatea. Chiar și atunci când nu discutăm cu copiii despre valorile culturale, aceștia vor prelua informații despre astfel de aspecte din comportamentul nostru.\n" +
                        "\n" +
                        "Gestionarea conflictelor\n" +
                        "Faptul că împărțim un spațiu restrâns cu familiile noastre face inevitabilă apariția conflictelor. Părinții nu au cum să fie mereu de acord între ei în toate privințele, însă copilul va fi martorul modului în care aceștia reușesc să depășească diferențele dintre ei. De asemenea, își va forma și niște modalități de reacție la conflict, în raport cu ceea ce învață de la părinții săi.\n" +
                        "\n" +
                        "℗PUBLICITATE\n" +
                        "\n" +
                        "\n" +
                        "Rolurile de gen\n" +
                        "Copiii observă modul în care adulții se raportează unii la ceilalți și, astfel, învață – încă de la o vârstă fragedă – tiparele de comportament specifice pentru femei și pentru bărbați. În același timp, învață cum să se raporteze la sexul opus.\n" +
                        "\n" +
                        "Tot în acest context, copilul definește „normalul“ în relația de cuplu, în funcție de comportamentele pe care le observă la părinți. Atunci când va deveni adult, își va construi relația cu partenerul de viață raportându-se la aceste aspecte învățate în copilărie.\n" +
                        "\n" +
                        "Controlul agresivității\n" +
                        "Contrar așteptărilor, copiii sunt observatori foarte fini ai părinților și sunt foarte conștienți de tensiunile dintre aceștia. Mai mult, apropierea specifică mediului familial face ca emoțiile să fie resimțite mai intens decât în alte contexte – fie că vorbim despre cele pozitive, fie de cele negative. Modul în care reacționezi când te supără copilul este foarte important, însă reține și faptul că el va observa și va copia abordarea pe care o aplici în relația cu partenerul.\n" +
                        "\n" +
                        "Empatia\n" +
                        "Copilul își dezvoltă empatia în măsura în care o experimentează în relațiile cu cei din jurul său. Cu cât părinții sunt mai empatici, cu atât copilul își dezvoltă mai mult această abilitate. El învață în mare parte din modele, prin observarea comportamentelor celorlalți. De aceea este important să-i furnizăm modele de comportament empatic.\n" +
                        "\n" +
                        "S-a constat faptul că, atunci când părinții își oferă sprijin reciproc și atenție, ei vor fi capabili să-i ofere aceste lucruri și copilului. Părinții între care există adesea tensiuni nerezolvate sunt mai puțin capabili să-i transmită copilului sentimentul de siguranță și baza unui atașament sigur. De aceea este recomandat ca, atunci când avem un conflict cu partenerul, să ne asigurăm că cei mici vor vedea și momentul împăcării. Astfel vor învăța că momentele dificile pot fi depășite și cel mai probabil vor ști ce să facă, la rândul lor, în situații similare."

        };

        for (int i = 0; i < idGenerator; i++) {
            mockArticlesContent.put((long) i, contents[i]);
        }
    }

    public News findArticlesById(long[] articlesIds) {
        List<Article> articles = new ArrayList<>();

        for (long id : articlesIds) {
            for (Article a : mockArticleDb.values()) {
                if (a.getId() == id) {
                    articles.add(a);
                    break;
                }
            }
        }

        return new News("ok", articles.size(), articles);
    }

    public void persistArticle(Article art, String content) {
        art.setId(idGenerator++);
        mockArticleDb.put(
                DateUtils.parseStringDateAndTime(art.getPublishAt()),
                art
        );

        mockArticlesContent.put(art.getId(), content);
    }
}
