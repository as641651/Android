function sec(){
  //Returns time in milliseconds. So we divide by 1000.
  return (new Date().getTime())/1000;
}

var s = sec();

function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function makeShape(){
  document.getElementById('shape').style.display = "block";

  //Set random size
  var width = 5 + Math.random()*25;
  var height = 5 + Math.random()*25;

  document.getElementById('shape').style.width = width + "%";
  document.getElementById('shape').style.height = height + "%";

  //Set random position
  //We subtract the size as the top left is used as anchor.
  //eg 100% top will place the top-left corner of shape at bottom and part of shape can be hidden
  var top = Math.random()*(95-height);
  var left = Math.random()*(95-width);

  document.getElementById('shape').style.top = top + "%";
  document.getElementById('shape').style.left = left + "%";

  document.getElementById('shape').style.backgroundColor = getRandomColor();

  //Make it a circle half the time
  if(Math.random() > 0.5){
      document.getElementById('shape').style.borderRadius = "50%";
  }else{
    document.getElementById('shape').style.borderRadius = "0%";
  }

  s = sec();
}


document.getElementById('shape').onclick = function(){
  document.getElementById('shape').style.display = "none";

  var diff = sec() - s;

  // ".toFixed" rounds diff to 2 decimal places
  document.getElementById('time-taken').innerHTML = diff.toFixed(2) + " sec";

  //Executes the function "makeShape" in atmost 2 sec
  setTimeout(makeShape,Math.random()*2000);
}
