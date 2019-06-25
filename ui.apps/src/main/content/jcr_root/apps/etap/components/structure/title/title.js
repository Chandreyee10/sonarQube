"use strict";

use(function () {

    var CONST = {
        PROP_TITLE: "jcr:title",
        PROP_TAG_TYPE: "type"
    }

    var title = {};
    
    // The actual title content retrieved from the property title
	// or, the pageProperties title, or, the currentPage.name
    title.text = properties.get(CONST.PROP_TITLE)
            || pageProperties.get(CONST.PROP_TITLE)
            || currentPage.name;

    
    return title;
    
});
