export interface IStudent {
    id?: number;
    firstName?: string;
    lastName?: string;
    age?: number;
}

export class Student implements IStudent {
    constructor(public id?: number, public firstName?: string, public lastName?: string, public age?: number) {}
}
