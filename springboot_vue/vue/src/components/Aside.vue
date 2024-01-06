<template>
   <div>

     <el-menu
         text-color="blue"
         background-color="pink"
         default-active="2"
         class="el-menu-vertical-demo"
         @open="handleOpen"
         @close="handleClose"
     >
       <el-menu-item index="1">
         <el-icon ><icon-menu /></el-icon>
         <span  @click="load">一键导入</span>
       </el-menu-item>


       <el-sub-menu index="2">
         <template #title>
           <el-icon><location /></el-icon>
           <span>分步select</span>
         </template>
         <el-menu-item-group title="Q6">
           <el-menu-item index="1-1" @click="q6">getAllStaffCount</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q7">
           <el-menu-item index="1-3" @click="q7">getContractCount</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q8">
           <el-menu-item index="1-3" @click="q8">getOrderCount</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q9">
           <el-menu-item index="1-3" @click="q9">getNeverSoldProduct</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q10">
           <el-menu-item index="1-3" @click="q10">getFavoriteProductModel</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q11">
           <el-menu-item index="1-3" @click="q11">getAvgStockByCenter</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q12">
           <el-menu-item index="1-3" @click="Q12first">getProductByNumber</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Q13">
           <el-menu-item index="1-3" @click ="Q13first">getContractInfo</el-menu-item>
         </el-menu-item-group>



       </el-sub-menu>

       <el-menu-item index="3" >
         <el-icon><document /></el-icon>
         <span @click="part">一键导出(Q6-11)</span>
       </el-menu-item>

       <el-menu-item index="4" >
         <el-icon><document /></el-icon>
         <span @click="allfirst">一键导出(Q6-13)</span>
       </el-menu-item>

       <el-menu-item index="5" >
         <el-icon><document /></el-icon>
         <span @click="origin">原始数据处理1</span>
       </el-menu-item>
       <el-menu-item index="6" >
         <el-icon><document /></el-icon>
         <span @click="stockin">stockin</span>
       </el-menu-item>
       <el-menu-item index="7" >
         <el-icon><document /></el-icon>
         <span @click="placeOrder">placeOrder</span>
       </el-menu-item>
       <el-menu-item index="8" >
         <el-icon><document /></el-icon>
         <span @click="UpdateOrder">UpdateOrder</span>
       </el-menu-item>
       <el-menu-item index="9" >
         <el-icon><document /></el-icon>
         <span @click="deleteOrder">deleteOrder</span>
       </el-menu-item>
       <el-menu-item index="9" >
         <el-icon><document /></el-icon>
         <span @click="OrignLoad">初始数据导入</span>
       </el-menu-item>

       <el-sub-menu index="10">
         <template #title>
           <el-icon><location /></el-icon>
           <span>Role</span>
         </template>
         <el-menu-item-group title="Boss">
           <el-menu-item index="1-1" @click="boss">toBoss</el-menu-item>
         </el-menu-item-group>

         <el-menu-item-group title="Worker">
           <el-menu-item index="1-3" @click="worker">toWorker</el-menu-item>
         </el-menu-item-group>

       </el-sub-menu>

     </el-menu>
<!--load-->
     <el-dialog
         v-model="dialogVisible"
         title="Tips"
         width="30%"
     >
       <span>Congratulations! Load succeed!</span>
       <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false"
        >Confirm</el-button
        >
      </span>
       </template>
     </el-dialog>
<!--     Q6-->
     <el-dialog v-model="Q6dialogTableVisible" title="result">
       <el-table :data="Q6tableData" height="250" style="width: 100%">
         <el-table-column prop="type" label="type"  />
         <el-table-column prop="count" label="count"  />
       </el-table>
     </el-dialog>
<!--Q7-->
     <el-dialog v-model="Q7dialogTableVisible" title="result">
       <el-table :data="Q7tableData" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>
     </el-dialog>
<!--     Q8-->
     <el-dialog v-model="Q8dialogTableVisible" title="result">
       <el-table :data="Q8tableData" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>
     </el-dialog>
<!--     Q9-->
     <el-dialog v-model="Q9dialogTableVisible" title="result">
       <el-table :data="Q9tableData" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>
     </el-dialog>
<!--     Q10-->
     <el-dialog v-model="Q10dialogTableVisible" title="result">
       <el-table :data="Q10tableData" height="250" style="width: 100%">
         <el-table-column prop="product_model" label="product_model"  />
         <el-table-column prop="quan" label="quan"  />
       </el-table>
     </el-dialog>
<!--     Q11-->
     <el-dialog v-model="Q11dialogTableVisible" title="result">
       <el-table :data="Q11tableData" height="250" style="width: 100%">
         <el-table-column prop="supply_center" label="supply_center"  />
         <el-table-column prop="numeric" label="numeric"  />
       </el-table>
     </el-dialog>
<!--     Q12-->
     <el-dialog
         v-model="dialogVisibleQ12"
         title="Tips"
         width="30%"
     >
      <el-form label-width="120px">
        <el-form-item model="form" label="model number">
          <el-input v-model="form.name"></el-input>
        </el-form-item>

      </el-form>
       <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="q12"
        >Confirm</el-button
        >
      </span>
       </template>
     </el-dialog>
   <el-dialog v-model="Q12dialogTableVisible" title="result">
       <el-table :data="Q12tableData" height="250" style="width: 100%">
         <el-table-column prop="supply_center" label="supply_center"  />
         <el-table-column prop="model" label="model"  />
         <el-table-column prop="quantity" label="quantity"  />
       </el-table>
     </el-dialog>
<!--     Q13-->
     <el-dialog
         v-model="dialogVisibleQ13"
         title="Tips"
         width="30%"
     >
       <el-form label-width="120px">
         <el-form-item model="form13" label="Contract">
           <el-input v-model="form13.name"></el-input>
         </el-form-item>

       </el-form>
       <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="q13"
        >Confirm</el-button
        >
      </span>
       </template>
     </el-dialog>


     <el-dialog v-model="Q13dialogTableVisible" title="result"  style="width: 100%">
       <!--     Q131-->
       <el-table :data="Q13tableData1"  style="width: 100%">
         <el-table-column prop="contract_num" label="contract_num"  />
         <el-table-column prop="name" label="name"  />
         <el-table-column prop="enterprise" label="enterprise"  />
         <el-table-column prop="supply_center" label="supply_center"  />
       </el-table>
<!--     Q132-->
       <el-table :data="Q13tableData2"  style="width: 100%">
         <el-table-column prop="product_model" label="product_model"  />
         <el-table-column prop="name" label="name"  />
         <el-table-column prop="quantity" label="quantity"  />
         <el-table-column prop="unit_price" label="unit_price"  />
         <el-table-column prop="estimated_delivery_date" label="estimated_delivery_date"  />
         <el-table-column prop="lodgement_date" label="lodgement_date"  />
       </el-table>
     </el-dialog>
<!--     一键导出6-11-->
     <el-dialog v-model="partdialogTableVisible" title="result">
       <el-table :data="Q6tableDatapart" height="250" style="width: 100%">
         <el-table-column prop="type" label="type"  />
         <el-table-column prop="count" label="count"  />
       </el-table>

       <el-table :data="Q7tableDatapart" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>

       <el-table :data="Q8tableDatapart" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>

       <el-table :data="Q9tableDatapart" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>


         <el-table :data="Q10tableDatapart" height="250" style="width: 100%">
           <el-table-column prop="product_model" label="product_model"  />
           <el-table-column prop="quan" label="quan"  />
         </el-table>

       <el-table :data="Q11tableDatapart" height="250" style="width: 100%">
         <el-table-column prop="supply_center" label="supply_center"  />
         <el-table-column prop="numeric" label="numeric"  />
       </el-table>


     </el-dialog>
<!--     一键导出6-13-->

     <el-dialog
         v-model="dialogVisibleAll"
         title="Tips"
         width="30%"
     >
       <el-form model="formAll" label-width="120px">
         <el-form-item  label="Contract">
           <el-input v-model="formAll.Contract"></el-input>
         </el-form-item>

         <el-form-item  label="Model">
           <el-input v-model="formAll.Model"></el-input>
         </el-form-item>
       </el-form>
       <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="all"
        >Confirm</el-button
        >
      </span>
       </template>
     </el-dialog>


     <el-dialog v-model="alldialogTableVisible" title="result">
       <el-table :data="Q6tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="type" label="type"  />
         <el-table-column prop="count" label="count"  />
       </el-table>

       <el-table :data="Q7tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>

       <el-table :data="Q8tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>

       <el-table :data="Q9tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="count" label="count"  />
       </el-table>


       <el-table :data="Q10tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="product_model" label="product_model"  />
         <el-table-column prop="quan" label="quan"  />
       </el-table>

       <el-table :data="Q11tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="supply_center" label="supply_center"  />
         <el-table-column prop="numeric" label="numeric"  />
       </el-table>

       <el-table :data="Q12tableDataAll" height="250" style="width: 100%">
         <el-table-column prop="supply_center" label="supply_center"  />
         <el-table-column prop="model" label="model"  />
         <el-table-column prop="quantity" label="quantity"  />
       </el-table>

       <el-table :data="Q13tableData1All"  style="width: 100%">
         <el-table-column prop="contract_num" label="contract_num"  />
         <el-table-column prop="name" label="name"  />
         <el-table-column prop="enterprise" label="enterprise"  />
         <el-table-column prop="supply_center" label="supply_center"  />
       </el-table>
       <!--     Q132-->
       <el-table :data="Q13tableData2All"  style="width: 100%">
         <el-table-column prop="product_model" label="product_model"  />
         <el-table-column prop="name" label="name"  />
         <el-table-column prop="quantity" label="quantity"  />
         <el-table-column prop="unit_price" label="unit_price"  />
         <el-table-column prop="estimated_delivery_date" label="estimated_delivery_date"  />
         <el-table-column prop="lodgement_date" label="lodgement_date"  />
       </el-table>
     </el-dialog>

<!--     origin-->
     <el-dialog
         v-model="dialogVisibleOrigin"
         title="Tips"
         width="30%"
     >
       <el-form label-width="120px">
         <el-form-item model="formQ1" label="input(#分隔)">
           <el-input v-model="formQ1.name"></el-input>
         </el-form-item>

       </el-form>
       <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="selectStaff">selectStaff</el-button>
         <el-button type="primary" @click="selectCenter">selectCenter</el-button>
         <el-button type="primary" @click="selectModel">selectModel</el-button>
        <el-button type="primary" @click="selectenterprise">selectenterprise</el-button>
        <el-button type="primary" @click="selectOrders">selectOrders</el-button>
         <el-button type="primary" @click="selectBill">showBill</el-button>
        <el-button type="primary" @click="others">others</el-button>
      </span>
       </template>
     </el-dialog>
<!--Staff-->
     <el-dialog v-model="StaffdialogTableVisible" title="result">
       <el-table :data="StafftableData" style="width: 100%">
         <el-table-column fixed prop="id" label="id" width="150" />
         <el-table-column prop="name" label="Name" width="120" />
         <el-table-column prop="age" label="age" width="120" />
         <el-table-column prop="gender" label="gender" width="120" />
         <el-table-column prop="number" label="number" width="120" />
         <el-table-column prop="supply_center" label="supply_center" width="130" />
         <el-table-column prop="mobile_number" label="mobile_number" width="150"/>
         <el-table-column prop="type" label="type" width="120"/>
       </el-table>
     </el-dialog>
     <!--center-->
     <el-dialog v-model="CenterdialogTableVisible" title="result">
       <el-table :data="CentertableData" style="width: 100%">
         <el-table-column fixed prop="id" label="id" width="150" />
         <el-table-column prop="name" label="Name" width="600" />
       </el-table>
     </el-dialog>
     <!--Enterprise-->
     <el-dialog v-model="EnterprisedialogTableVisible" title="result">
       <el-table :data="EnterprisetableData" style="width: 100%">
         <el-table-column fixed prop="id" label="id" width="150" />
         <el-table-column prop="name" label="Name" width="600" />
         <el-table-column prop="country" label="country" width="600" />
         <el-table-column prop="city" label="city" width="600" />
         <el-table-column prop="supply_center" label="supply_center" width="600" />
         <el-table-column prop="industry" label="industry" width="600" />
       </el-table>
     </el-dialog>
     <!--model-->
     <el-dialog v-model="ModeldialogTableVisible" title="result">
       <el-table :data="ModeltableData" style="width: 100%">
         <el-table-column fixed prop="id" label="id" width="150" />
         <el-table-column prop="number" label="number" width="600" />
         <el-table-column prop="model" label="model" width="600" />
         <el-table-column prop="name" label="name" width="600" />
         <el-table-column prop="unit_price" label="unit_price" width="600" />
       </el-table>
     </el-dialog>
     <!--orders-->
<!--     //orders(id,contract_num,enterprise,product_model,quantity,contract_manager,contract_date,-->
<!--     // estimated_delivery_date, lodgement_date,salesman_num,contract_type)-->
     <el-dialog v-model="OrdersdialogTableVisible" title="result">
       <el-table :data="OrderstableData" style="width: 100%">
         <el-table-column fixed prop="id" label="id" width="150" />
         <el-table-column prop="contract_num" label="contract_num" width="600" />
         <el-table-column prop="enterprise" label="enterprise" width="600" />
         <el-table-column prop="product_model" label="product_model" width="600" />
         <el-table-column prop="quantity" label="quantity" width="600" />
         <el-table-column prop="contract_manager" label="contract_manager" width="600" />
         <el-table-column prop="contract_date" label="contract_date" width="600" />
         <el-table-column prop="estimated_delivery_date" label="estimated_delivery_date" width="600" />
         <el-table-column prop="lodgement_date" label="lodgement_date" width="600" />
         <el-table-column prop="salesman_num" label="salesman_num" width="600" />
         <el-table-column prop="contract_type" label="contract_type" width="600" />

       </el-table>
     </el-dialog>
     <!--bill-->
     <el-dialog v-model="billdialogTableVisible" title="result">
       <el-table :data="billtableData" style="width: 100%">
         <el-table-column fixed prop="order_id" label="order_id" width="150" />
         <el-table-column prop="model" label="model" width="600" />
         <el-table-column prop="profit" label="profit" width="600" />
       </el-table>
     </el-dialog>
   </div>
</template>
<script>
import request from "../../utils/request";

export default {
  name: "Aside",
  components:{},
  data(){
    return{
      StafftableData:[],
      StaffdialogTableVisible:false,
      dialogVisibleOrigin:false,
      Q6tableData:[],
      Q6dialogTableVisible:false,
      Q7tableData:[],
      Q7dialogTableVisible:false,
      Q8tableData:[],
      Q8dialogTableVisible:false,
      Q9tableData:[],
      Q9dialogTableVisible:false,
      Q10tableData:[],
      Q10dialogTableVisible:false,
      Q11tableData:[],
      Q11dialogTableVisible:false,
      Q12tableData:[],
      Q12dialogTableVisible:false,
      Q13tableData1:[],
      Q13tableData2:[],
      Q13dialogTableVisible:false,
      dialogVisible:false,
      dialogVisibleQ12:false,
      dialogVisibleQ13:false,
      form:{},
      form13:{},
      formQ1:{},

      partdialogTableVisible:false,
      Q6tableDatapart:[],
      Q7tableDatapart:[],
      Q8tableDatapart:[],
      Q9tableDatapart:[],
      Q10tableDatapart:[],
      Q11tableDatapart:[],

      dialogVisibleAll:false,
      formAll:[],

      alldialogTableVisible:false,
      Q6tableDataAll:[],
      Q7tableDataAll:[],
      Q8tableDataAll:[],
      Q9tableDataAll:[],
      Q10tableDataAll:[],
      Q11tableDataAll:[],
      Q12tableDataAll:[],
      Q13tableData1All:[],
      Q13tableData2All:[],

      CenterdialogTableVisible:false,
      CentertableData:[],
      EnterprisedialogTableVisible:false,
      EnterprisetableData:[],
      ModeldialogTableVisible:false,
      ModeltableData:[],
      OrdersdialogTableVisible:false,
      OrderstableData:[],
      billdialogTableVisible:false,
      billtableData:[],

    }
  },
  methods:{
    stockin(){
      request.post("/center/stockin").then(res=>{
      })
    },
    placeOrder(){
      request.post("/center/placeOrder").then(res=>{
      })
    },
    UpdateOrder(){
      request.post("/center/UpdateOrder").then(res=>{
      })
    },
    deleteOrder(){
      request.post("/center/deleteOrder").then(res=>{
      })
    },
    OrignLoad(){
      request.post("/center/OrignLoad").then(res=>{
      })
    },
    worker(){
      request.post("/center/worker").then(res=>{
      })
    },
    boss(){
      request.post("/center/boss").then(res=>{
      })
    },
    others(){
      this.dialogVisibleOrigin=false
      request.get("/center/others?search="+this.formQ1.name).then(res=> {
      })
    },
    selectBill(){
      this.dialogVisibleOrigin=false
      request.get("/center/selectBill").then(res=> {
        this.billtableData=res
      })
      this.billdialogTableVisible=true
    },
    selectOrders(){
      this.dialogVisibleOrigin=false
      request.get("/center/selectOrders?search="+this.formQ1.name).then(res=> {
        this.OrderstableData=res
      })
      this.OrdersdialogTableVisible=true
    },
    selectenterprise(){
      this.dialogVisibleOrigin=false
      request.get("/center/selectenterprise?search="+this.formQ1.name).then(res=> {
       this.EnterprisetableData=res
      })
      this.EnterprisedialogTableVisible=true
    },
    selectModel(){
      this.dialogVisibleOrigin=false
      request.get("/center/selectModel?search="+this.formQ1.name).then(res=> {
        this.ModeltableData=res
      })
      this.ModeldialogTableVisible=true
    },
    selectCenter(){
      this.dialogVisibleOrigin=false
      request.get("/center/selectCenter?search="+this.formQ1.name).then(res=> {
        this.CentertableData=res
      })
      this.CenterdialogTableVisible=true
    },
    selectStaff(){
      this.dialogVisibleOrigin=false
      request.get("/center/selectStaff?search="+this.formQ1.name).then(res=> {
        this.StafftableData=res
      })
      this.StaffdialogTableVisible=true

    },
    origin(){
      this.formQ1={}
      this.dialogVisibleOrigin=true
    },
    allfirst(){
      this.formAll={}
      this.dialogVisibleAll=true
    },
    all(){
      this.dialogVisibleAll=false;
      request.get("/center/Q6").then(res=> {
        this.Q6tableDataAll=res
      })
      request.get("/center/Q7").then(res=> {
        this.Q7tableDataAll=res
      })
      request.get("/center/Q8").then(res=> {
        this.Q8tableDataAll=res
      })
      request.get("/center/Q9").then(res=> {
        this.Q9tableDataAll=res
      })
      request.get("/center/Q10").then(res=> {
        this.Q10tableDataAll=res
      })
      request.get("/center/Q11").then(res=> {
        this.Q11tableDataAll=res
      })
      request.get("/center/Q12?search="+this.formAll.Model).then(res=> {
        this.Q12tableDataAll=res
      })
      request.get("/center/Q132?search="+this.formAll.Contract).then(res=> {
        this.Q13tableData2All=res
      })
      request.get("/center/Q131?search="+this.formAll.Contract).then(res=> {
        this.Q13tableData1All=res
      })
      this.alldialogTableVisible=true;
    },
    part(){
      request.get("/center/Q6").then(res=> {
        this.Q6tableDatapart=res
      })
      request.get("/center/Q7").then(res=> {
        this.Q7tableDatapart=res
      })
      request.get("/center/Q8").then(res=> {
        this.Q8tableDatapart=res
      })
      request.get("/center/Q9").then(res=> {
        this.Q9tableDatapart=res
      })
      request.get("/center/Q10").then(res=> {
        this.Q10tableDatapart=res
      })
      request.get("/center/Q11").then(res=> {
        this.Q11tableDatapart=res
      })
      this.partdialogTableVisible=true;
    },
    q13(){
      this.dialogVisibleQ13=false;
      request.get("/center/Q131?search="+this.form13.name).then(res=> {
        this.Q13tableData1=res
      })
      request.get("/center/Q132?search="+this.form13.name).then(res=> {
        this.Q13tableData2=res
      })

      this.Q13dialogTableVisible=true},
    Q13first(){
      this.form13={}
      this.Q13tableData2=[]
      this.Q13tableData1=[]
      this.dialogVisibleQ13=true
    },
    Q12first() {
      this.form={}
      this.dialogVisibleQ12=true;

    },
    q12(){
      this.dialogVisibleQ12=false;
      request.get("/center/Q12?search="+this.form.name).then(res=> {
      this.Q12tableData=res
    })
      this.Q12dialogTableVisible=true},
    q11(){request.get("/center/Q11").then(res=> {
      this.Q11tableData=res
    })
      this.Q11dialogTableVisible=true},
    q10(){request.get("/center/Q10").then(res=> {
      this.Q10tableData=res
    })
      this.Q10dialogTableVisible=true},
    q9(){request.get("/center/Q9").then(res=> {
      this.Q9tableData=res
    })
      this.Q9dialogTableVisible=true},
    q8(){request.get("/center/Q8").then(res=> {
      this.Q8tableData=res
    })
      this.Q8dialogTableVisible=true},
    q7(){request.get("/center/Q7").then(res=> {
      this.Q7tableData=res
    })
      this.Q7dialogTableVisible=true},

    q6(){
      request.get("/center/Q6").then(res=> {
        this.Q6tableData=res
      })
      this.Q6dialogTableVisible=true

    },
    load(){
     this.dialogVisible=true
      request.post("/center/load").then(res=>{
      })
    }
  }
}


</script>

<style scoped>

</style>