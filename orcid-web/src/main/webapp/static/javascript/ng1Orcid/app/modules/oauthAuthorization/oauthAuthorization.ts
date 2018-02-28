import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { CommonNg2Module }
    from './../common/common.ts';

import { OauthAuthorizationComponent } 
    from './oauthAuthorization.component.ts';

// This is the Angular 1 part of the module
export const OauthAuthorizationModule = angular.module(
    'OauthAuthorizationModule', 
    []
);

// This is the Angular 2 part of the module

@NgModule(
    {
        declarations: [
            OauthAuthorizationComponent
        ],
        entryComponents: [ 
            OauthAuthorizationComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
        ]
    }
)
export class OauthAuthorizationNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives

OauthAuthorizationModule.directive(
    'oauthAuthorizationNg2', 
    <any>downgradeComponent(
        {
            component: OauthAuthorizationComponent
        }
    )
);
