import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';


@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  currentYear = new Date().getFullYear();

  features = [
  {
    icon: 'bi-bank2', // Bootstrap equivalent of Landmark
    title: 'Easy Account Management',
    description: 'Open savings or term accounts in minutes. Manage your finances with a clear and simple interface.'
  },
  {
    icon: 'bi-phone', // Bootstrap equivalent of Smartphone
    title: 'Seamless Transactions',
    description: 'Deposit, withdraw, and transfer funds effortlessly. Your balance updates in real-time, anytime, anywhere.'
  },
  {
    icon: 'bi-robot', // Bootstrap equivalent of Bot
    title: 'AI-Powered Insights',
    description: 'Get smart summaries of your spending patterns and savings trends to make informed financial decisions.'
  },
  {
    icon: 'bi-shield-check', // Bootstrap equivalent of ShieldCheck
    title: 'Secure & Reliable',
    description: 'Your security is our priority. We use top-tier encryption and security protocols to protect your data.'
  }
];

}
