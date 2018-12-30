import { NgModule } from '@angular/core';

import { FileUploadDemoSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [FileUploadDemoSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [FileUploadDemoSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class FileUploadDemoSharedCommonModule {}
