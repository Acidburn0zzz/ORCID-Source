import * as angular 
    from 'angular';

import { Directive, NgModule } 
    from '@angular/core';

import { downgradeComponent, UpgradeModule } 
    from '@angular/upgrade/static';

//User generated components
import { AffiliationFormComponent } 
    from './affiliationForm.component.ts';

import { CommonNg2Module }
    from './../common/common.ts';

// This is the Angular 1 part of the module
export const AffiliationFormModule = angular.module(
    'AffiliationFormModule', 
    []
);

// This is the Angular 2 part of the module
@NgModule(
    {
        declarations: [ 
            AffiliationFormComponent
        ],
        entryComponents: [ 
            AffiliationFormComponent 
        ],
        imports: [
            CommonNg2Module
        ],
        providers: [
            
        ]
    }
)
export class AffiliationFormNg2Module {}

// components migrated to angular 2 should be downgraded here
//Must convert as much as possible of our code to directives
AffiliationFormModule.directive(
    'affiliationFormNg2', 
    <any>downgradeComponent(
        {
            component: AffiliationFormComponent
        }
    )
);
