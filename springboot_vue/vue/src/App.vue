<template>
<!--  <div>-->
<!--    <img class="lg"-->
<!--         src="./assets/bj.jpg" >-->
<!--  </div>-->

  <div>
    <div ><Header/></div>
    <div style="display: flex"><Aside/></div>
  </div>

</template>

<style>
body{
  margin: 0;
}
.lg{
  width :100%;
  height:auto;
  overflow: hidden;
  position: absolute;
  top:0;
  z-index: 0;

}
.lg img{
    width :100%;
}
</style>

<script>
import Header from "@/components/Header";
import Aside from "@/components/Aside";



export default{
  name: "Layout",
  components:{
    Header,
    Aside,
  },
  data(){
  }

}
</script>