/// Copyright (c) 2009 Microsoft Corporation 
/// 
/// Redistribution and use in source and binary forms, with or without modification, are permitted provided
/// that the following conditions are met: 
///    * Redistributions of source code must retain the above copyright notice, this list of conditions and
///      the following disclaimer. 
///    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and 
///      the following disclaimer in the documentation and/or other materials provided with the distribution.  
///    * Neither the name of Microsoft nor the names of its contributors may be used to
///      endorse or promote products derived from this software without specific prior written permission.
/// 
/// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
/// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
/// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
/// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
/// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
/// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
/// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
/// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 

/*
create sets the [[Prototype]] of the created object to first parameter.
This can be checked using isPrototypeOf, or getPrototypeOf.
*/

ES5Harness.registerTest( {
id: "15.2.3.5-4-1",

path: "TestCases/chapter15/15.2/15.2.3/15.2.3.5/15.2.3.5-4-1.js",

description: "Object.create sets the prototype of the passed-in object and adds new properties",

test: function testcase() {
    function base() {}
    var b = new base();
    var prop = new Object();
    var d = Object.create(b,{ "x": {value: true,writable: false},
                              "y": {value: "str",writable: false} });

    if (Object.getPrototypeOf(d) === b &&
        b.isPrototypeOf(d) === true &&
        d.x === true &&
        d.y === "str" &&
        b.x === undefined &&
        b.y === undefined) {
      return true;
    }
 },

precondition: function prereq() {
  return fnExists(Object.create) && fnExists(Object.getPrototypeOf);
 }
});
