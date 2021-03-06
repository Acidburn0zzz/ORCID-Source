//not used

declare var $window: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { StaticsService } 
    from '../../shared/statics.service.ts';


@Component({
    selector: 'statics-ng2',
    template:  scriptTmpl("statics-ng2-template")
})
export class StaticsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    liveIds: any;

    constructor(
        private staticsService: StaticsService
    ) {
        this.liveIds = 0;
    }

    getLiveIds(): void {

        this.staticsService.getLiveIds()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.liveIds = data;
            },
            error => {
                //console.log('getWebsitesFormError', error);
            } 
        );
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getLiveIds();
    }; 
}