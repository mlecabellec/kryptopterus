/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//Require  lodash, JQuery , jquery-template, papaparse


this["APP"] = this["APP"] || {
    moduleInfo:
            {
                moduleId: "APP",
                moduleVersion: "0.0.1-DEV",
                description: "Root of the APP tree"
            },
    loadedModules: [],
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.moduleInfo);
    }
};
APP.bootstrap();
//------------------------------------------------------------------------------

this.APP["login"] = this.APP["login"] || {
    moduleInfo:
            {
                moduleId: "APP.Login",
                moduleVersion: "0.0.2-DEV",
                description: "Login related module"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.login.moduleInfo);
    },
    gui: {
        init: function init()
        {

            $("#doLoginButton").unbind("click");
            $("#doLoginButton").on("click", APP.login.gui.displayLoginModal);
            //$("#loginButton").unbind("click");
            //$("#loginButton").on("click", APP.login.gui.doLoginStage1);
            APP.login.gui.updateMe();
        },
        displayLoginModal: function displayLoginModal()
        {

            //$("#loginModal").modal();

            $("#modalContainer").load("res/templates/loginModal.html", {}, function doLoginStage0() {

                $("#loginButton").unbind("click");
                $("#loginButton").on("click", APP.login.gui.doLoginStage1);
                $("#loginModal").modal();

            });



        },
        doLoginStage1: function doLoginStage1()
        {

            $("#loginModal").modal('hide');
            var me = {
                username: $("#usernameField").val(),
                password: $("#password1Field").val()
            };
            $("#password1Field").val("");
            var loginJqxhr = $.ajax({
                url: "s/home/me",
                method: "POST",
                cache: false,
                data: JSON.stringify(me),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: false,
                success: APP.login.gui.doLoginStage2
            });
        },
        doLoginStage2: function doLoginStage2(me)
        {
            $("#meLabel").text(me.username);
            APP.login.data.me = me;
            //TODO ?

            if (me.hasAuthenticationFailed)
            {
                $("#jumbotron").fadeOut(3000);
                var alertDiv = $("<div/>").addClass("alert alert-danger");
                alertDiv.addClass("authAlert");
                alertDiv.attr("role", "alert");
                alertDiv.text(me.flashMessage);
                alertDiv.hide();

                alertDiv.appendTo("#messageContainer");
                alertDiv.fadeIn(1200);

            } else
            {
                $("div.authAlert").remove();

                if ($(".alert alert-danger").length == 0)
                {
                    $("#jumbotron").fadeIn(300);
                }


            }

        },
        updateMe: function updateMe()
        {
            var meJqxhr = $.ajax({
                url: "s/home/me",
                method: "GET",
                cache: false,
                dataType: "json",
                crossDomain: false,
                success: APP.login.gui.updateMeLabel
            });
            //TODO ?

        },
        updateMeLabel: function updateMeLabel(me)
        {
            $("#meLabel").text(me.username);
            APP.login.data.me = me;


            //TODO ?

        }
    },
    data: {
        me: {}
    }
};
APP.login.bootstrap();
//------------------------------------------------------------------------------

this.APP["debug"] = this.APP["debug"] || {
    moduleInfo:
            {
                moduleId: "APP.debug",
                moduleVersion: "0.0.1-DEV",
                description: "Debug related functions"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.debug.moduleInfo);
        if (APP.debug.enabled)
        {
            APP.debug.debugKit.init();
        }
    },
    enabled: true,
    debugKit: {
        init: function initDebugKit()
        {

        }
    }
};
APP.debug.bootstrap();
//------------------------------------------------------------------------------


this.APP["i18n"] = this.APP["i18n"] || {
    moduleInfo:
            {
                moduleId: "APP.i18n",
                moduleVersion: "0.0.1-DEV",
                description: "i18n related functions"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.i18n.moduleInfo);
    },
    gui: {
        init: function init()
        {
            var detectedUserLanguage = navigator.language || navigator.userLanguage;
            //TODO


        },
        scanPageForPopulate: function scanPageForPopulate() {
            //scan the page in order to index every text parts
            //Every element should be populated with the targetCOntext 'page'
            //context, internalIdentfier , cssSelector, content
        },
        translatePageElement: function translatePageElement(jqueryElement,targetContext)
        {
            //Translate every texts found using populated data and loaded data to targetContext (aka language)
            //match on cssSelector
        },
        t:function t(identifier,context){
            //Just returns the content related to an identifier and a context
        }
    },
    data: {}
};
APP.i18n.bootstrap();
//------------------------------------------------------------------------------

this.APP["logs"] = this.APP["logs"] || {
    moduleInfo:
            {
                moduleId: "APP.logs",
                moduleVersion: "0.0.1-DEV",
                description: "Log related functions"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.logs.moduleInfo);
    }
};
APP.logs.bootstrap();
//------------------------------------------------------------------------------

this.APP["home"] = this.APP["home"] || {
    moduleInfo:
            {
                moduleId: "APP.home",
                moduleVersion: "0.0.2-DEV",
                description: "Home functions including init routines"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.home.moduleInfo);
    },
    gui: {
        init: function init() {

            //$("#changeAndImpactsLink").unbind("click");
            //$("#changeAndImpactsLink").on("click", APP.home.gui.setupChangeAndImpactsWorkshop);

            //APP.home.gui.initQuillEditor_v1();
            //APP.home.gui.getMessages(0, 50);
        }
    },
    toolKit: {},
    data: {}
};
APP.home.bootstrap();


//------------------------------------------------------------------------------

this.APP["activityList"] = this.APP["activityList"] || {
    moduleInfo:
            {
                moduleId: "APP.activities",
                moduleVersion: "0.0.1-DEV",
                description: "Activity related functions including init routines"
            },
    bootstrap: function bootstrap() {
        APP.loadedModules = APP.loadedModules.concat(APP.activityList.moduleInfo);
    },
    gui: {
        init: function init() {

            $("#displayActivityList").unbind("click");
            $("#displayActivityList").on("click", APP.activityList.gui.displayActivityList);

        },
        displayActivityList: function displayActivityList()
        {
            $("#jumbotron>h1").text("Let's go...");
            $("#jumbotron").fadeOut();
            $("#jumbotron>h1").text("Bienvenue !!");



        }
    },
    toolKit: {},
    data: {}
};
APP.activityList.bootstrap();




$(function onReadyHandler()
{
    APP.i18n.gui.init();
    APP.login.gui.init();
    APP.home.gui.init();
    APP.activityList.gui.init();
});
