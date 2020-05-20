<nav class="navbar navbar-expand-sm bg-dark fixed-top navbar-dark">
    <!-- Brand -->
    <!--<a class="navbar-brand" href="index">SAF Builder</a>-->
    <a href="index" class="navbar-brand">
        <img src="<%= request.getContextPath()%>/logo.png" />
    </a>

    <button type="button" class="navbar-toggler" data-toggle="collapse" data-target="#navbarCollapse">
        <span class="navbar-toggler-icon"></span>
    </button>


    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link" href="#">SAF</a>
        </li>
        <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbardrop" data-toggle="dropdown">CS</a>
            <div class="dropdown-menu">
                <a class="dropdown-item" href="csProcess">SAF</a>
                <a class="dropdown-item" href="csTaxonomy">Taxonomy</a>
            </div>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="index">Help</a>
        </li>
    </ul>
</nav>