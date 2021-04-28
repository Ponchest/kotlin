type Nullable<T> = T | null | undefined
export class Module {
    constructor();
    readonly _any: any;
    _nothing(): never;
    readonly _throwable: Error;
    readonly _string: string;
    readonly _boolean: boolean;
    readonly _byte: number;
    readonly _short: number;
    readonly _int: number;
    readonly _float: number;
    readonly _double: number;
    readonly _byte_array: Int8Array;
    readonly _short_array: Int16Array;
    readonly _int_array: Int32Array;
    readonly _float_array: Float32Array;
    readonly _double_array: Float64Array;
    readonly _array_byte: Array<number>;
    readonly _array_short: Array<number>;
    readonly _array_int: Array<number>;
    readonly _array_float: Array<number>;
    readonly _array_double: Array<number>;
    readonly _array_string: Array<string>;
    readonly _array_boolean: Array<boolean>;
    readonly _array_array_string: Array<Array<string>>;
    readonly _array_array_int_array: Array<Array<Int32Array>>;
    readonly _fun_unit: () => void;
    readonly _fun_int_unit: (p0: number) => void;
    readonly _fun_boolean_int_string_intarray: (p0: boolean, p1: number, p2: string) => Int32Array;
    readonly _curried_fun: (p0: number) => (p0: number) => (p0: number) => (p0: number) => (p0: number) => number;
    readonly _higher_order_fun: (p0: (p0: number) => string, p1: (p0: string) => number) => (p0: number) => number;
    readonly _n_any: Nullable<any>;
    readonly _n_nothing: Nullable<never>;
    readonly _n_throwable: Nullable<Error>;
    readonly _n_string: Nullable<string>;
    readonly _n_boolean: Nullable<boolean>;
    readonly _n_byte: Nullable<number>;
    readonly _n_short_array: Nullable<Int16Array>;
    readonly _n_array_int: Nullable<Array<number>>;
    readonly _array_n_int: Array<Nullable<number>>;
    readonly _n_array_n_int: Nullable<Array<Nullable<number>>>;
    readonly _array_n_array_string: Array<Nullable<Array<string>>>;
    readonly _fun_n_int_unit: (p0: Nullable<number>) => void;
    readonly _fun_n_boolean_n_int_n_string_n_intarray: (p0: Nullable<boolean>, p1: Nullable<number>, p2: Nullable<string>) => Nullable<Int32Array>;
    readonly _n_curried_fun: (p0: Nullable<number>) => (p0: Nullable<number>) => (p0: Nullable<number>) => Nullable<number>;
    readonly _n_higher_order_fun: (p0: (p0: Nullable<number>) => Nullable<string>, p1: (p0: Nullable<string>) => Nullable<number>) => (p0: Nullable<number>) => Nullable<number>;
}
