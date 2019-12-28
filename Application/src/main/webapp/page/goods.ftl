<#import "../part/common.ftl" as c>
<@c.login title="Корзина">
    <main class="page projects-page" style="margin-top: 100px; margin-bottom: 20px">
        <section class="portfolio-block projects-cards">
            <div class="container">
                <div class="row">
                    <#list goods as good>
                        <div class="col-md-6 col-lg-4">
                            <div class="card border-0">
                                <div class="card-body">
                                    <h6><a href="#">${good.name!"defName"}</a></h6>
                                    <p class="text-muted card-text">${good.price!"сто тысяч миллионов"}</p>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </section>
    </main>

</@c.login>