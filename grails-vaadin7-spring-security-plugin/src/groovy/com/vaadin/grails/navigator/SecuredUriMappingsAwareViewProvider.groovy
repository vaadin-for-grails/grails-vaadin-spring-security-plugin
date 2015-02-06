package com.vaadin.grails.navigator

import com.vaadin.grails.Vaadin
import com.vaadin.grails.security.ui.LoginView
import com.vaadin.grails.security.ui.NotAuthorizedView
import com.vaadin.grails.server.SecurityAwareUriMappingsHolder
import com.vaadin.navigator.View
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Checks <code>UriMappings</code> for <code>access</code> properties.
 * <p>
 *     If there is an <code>access</code> property and the current user is not logged in,
 *     a {@link com.vaadin.grails.security.ui.LoginComponent} is shown.
 * </p>
 * <p>
 *     If there is an <code>access</code> property and the current user is not granted,
 *     a {@link com.vaadin.grails.security.ui.NotAuthorizedComponent} is shown.
 * </p>
 *
 * @author Stephan Grundner
 */
class SecuredUriMappingsAwareViewProvider extends UriMappingsAwareViewProvider {

    Class<? extends View> getLoginViewClass() {
        LoginView
    }

    Class<? extends View> getNotAuthorizedViewClass() {
        NotAuthorizedView
    }

    String[] getRoles(String fragment) {
        uriMappings.getFragmentProperty(path, fragment, SecurityAwareUriMappingsHolder.ACCESS_FRAGMENT_PROPERTY)
    }

    @Override
    View getView(String fragment) {
        if (fragment == "") {
            fragment = getDefaultFragment(path)
        }

        def roles = getRoles(fragment)
        if (roles?.length > 0) {
            def securityService = Vaadin.getInstance(SpringSecurityService)
            if (!securityService.isLoggedIn()) {
                return Vaadin.newInstance(loginViewClass)
            } else if (!SpringSecurityUtils.ifAnyGranted(roles.join(","))) {
                return Vaadin.newInstance(notAuthorizedViewClass)
            } else {
//                granted!
            }
        }

        super.getView(fragment)
    }
}
