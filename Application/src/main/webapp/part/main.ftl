<#macro opportunity header text picture>
    <div class="col-md-4"><span class="fa-stack fa-4x"><i class="fa fa-circle fa-stack-2x text-primary"></i>
                        <i class="fa fa-${picture} fa-stack-1x fa-inverse"></i></span>
        <h4 class="section-heading">${header}</h4>
        <p class="text-muted">${text}</p>
    </div>
</#macro>

<#macro category number picture name>
    <div class="col-sm-6 col-md-4 portfolio-item">
        <a class="portfolio-link" data-toggle="modal" href="#portfolioModal${number}">
            <div class="portfolio-hover">
                <div class="portfolio-hover-content"></div>
            </div><img class="img-fluid" src="../assets/img/portfolio/${picture}.jpg"></a>
        <div class="portfolio-caption">
            <h4>${name}</h4>
        </div>
    </div>
</#macro>

<#macro developer picture name role>
    <div class="col-sm-4">
        <div class="team-member"><img class="rounded-circle mx-auto" src="../assets/img/team/${picture}.jpg">
            <h4>${name}</h4>
            <p class="text-muted">${role}</p>
        </div>
    </div>
</#macro>