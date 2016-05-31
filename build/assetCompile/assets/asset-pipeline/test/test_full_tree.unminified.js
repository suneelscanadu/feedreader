//=require_self
//=require_full_tree libs

console.log("Initial Testing Stack for dependency load order");

console.log("This is File A!");

console.log("This is File C");

//=require file_c

console.log("This is File B");

console.log("Subset A");

