import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import './App.css'
import CustomerComponent from './components/CustomerComponent'
import FooterComponent from './components/FooterComponent'
import HeaderComponent from './components/HeaderComponent'
import ItemComponent from './components/ItemComponent'
import ListItemsComponent from './components/ListItemsComponent'
import LoginComponent from './components/LoginComponent'
import ManageCustomerComponent from './components/ManageCustomersComponent'
import ManageStaffComponent from './components/ManageStaffComponent'
import RegisterComponent from './components/RegisterComponent'
import StaffComponent from './components/StaffComponent'
import TaxRateComponent from './components/TaxRateComponent'

import InventoryComponent from './components/InventoryComponent'
import ListRecipesComponent from './components/ListRecipesComponent'
import RecipeComponent from './components/RecipeComponent'

import ListOrdersComponent from './components/ListOrdersComponent'
import PickupOrderComponent from './components/PickupOrderComponent'
import PlaceOrderComponent from './components/PlaceOrderComponent'


import OrdersTableComponent from './components/FullfillOrdersComponent'
import { isAdminUser, isCustomerUser, isGuestUser, isStaffUser, isUserLoggedIn } from './services/AuthService'

function App() {

  function AuthenticatedRoute({children}) {
    const isAuth = isUserLoggedIn()
	if (isAuth) {
	  return children
	}
	return <Navigate to='/' />
  }

  return (
    <>
      <BrowserRouter>
	  <HeaderComponent />
	  <Routes>
	  	<Route path='/' element={<LoginComponent />}></Route>
		<Route path='/register' element={<RegisterComponent />}></Route>
		<Route path='/login' element={<LoginComponent />}></Route>

		{isAdminUser() && (<Route path='/taxrate' element={<AuthenticatedRoute><TaxRateComponent /></AuthenticatedRoute>}></Route>)}
		{isAdminUser() && (<Route path= '/manage-staff' element = {<AuthenticatedRoute><ManageStaffComponent /> </AuthenticatedRoute>}></Route>)}
		{isAdminUser() && (<Route path= '/create-staff' element = {<AuthenticatedRoute><StaffComponent /> </AuthenticatedRoute>}></Route>)}
		{isAdminUser() && (<Route path= '/update-staff/:id' element = {<AuthenticatedRoute><StaffComponent /> </AuthenticatedRoute>}></Route>)}
		{isAdminUser() && (<Route path= '/manage-customers' element = {<AuthenticatedRoute><ManageCustomerComponent /> </AuthenticatedRoute>}></Route>)}
		{isAdminUser() && (<Route path= '/update-customer/:id' element = {<AuthenticatedRoute><CustomerComponent /> </AuthenticatedRoute>}></Route>)}
		

		{isStaffUser() && (<Route path='/inventory' element={<AuthenticatedRoute><InventoryComponent /></AuthenticatedRoute>}></Route>)}
		{isStaffUser() && (<Route path='/add-recipe' element={<AuthenticatedRoute><RecipeComponent /></AuthenticatedRoute>}></Route>)};
		{isStaffUser() && (<Route path='/recipes' element={<AuthenticatedRoute><ListRecipesComponent /></AuthenticatedRoute>}></Route>)};
		{isStaffUser() && (<Route path='/edit-recipe/:id' element={<AuthenticatedRoute><RecipeComponent /></AuthenticatedRoute>}></Route>)};
		{isStaffUser() && (<Route path='/items' element={<AuthenticatedRoute><ListItemsComponent /></AuthenticatedRoute>}></Route>)};
		{isStaffUser() && (<Route path='/add-item' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>)};
		{isStaffUser() && (<Route path='/update-item/:id' element={<AuthenticatedRoute><ItemComponent /></AuthenticatedRoute>}></Route>)};
		{isStaffUser() && (<Route path='/order-table' element={<AuthenticatedRoute><OrdersTableComponent /></AuthenticatedRoute>}></Route>)};


		{(isCustomerUser() || isGuestUser()) && (<Route path='/place-order' element={<AuthenticatedRoute><PlaceOrderComponent /></AuthenticatedRoute>}></Route>)};
		{isCustomerUser() && (<Route path='/orders' element={<AuthenticatedRoute><ListOrdersComponent /></AuthenticatedRoute>}></Route>)};
		{isCustomerUser() && (<Route path='/pickup-order' element={<AuthenticatedRoute><PickupOrderComponent /></AuthenticatedRoute>}></Route>)};


	  </Routes>
	  <FooterComponent />
	  </BrowserRouter>
    </>
  )
}

export default App
