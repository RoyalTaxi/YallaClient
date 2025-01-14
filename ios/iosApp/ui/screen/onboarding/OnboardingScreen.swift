//
//  OnboardingScreen.swift
//  iosApp
//
//  Created by Islom on 14/10/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct OnboardingScreen: View {

    private struct Page {
        let image: String
        let title: String
        let description: String
    }

    private let pages = [
        Page(image: "img_onboarding_1", title: "Welcome", description: "Welcome to our app."),
        Page(image: "img_onboarding_2", title: "Explore", description: "Explore new features."),
        Page(image: "img_onboarding_3", title: "Connect", description: "Connect with others."),
        Page(image: "img_onboarding_4", title: "Get Started", description: "Let's get started!")
    ]

    @State private var currentPage = 0

    var body: some View {
        VStack {
            Spacer().frame(height: 43)

            TabView(selection: $currentPage) {
                ForEach(0..<pages.count, id: \.self) { index in
                    VStack {
                        Image(pages[index].image)
                            .resizable()
                            .scaledToFit()
                            .frame(width: .infinity)
                            .padding(.top, 20)

                        Spacer().frame(height: 54)

                        Text(pages[index].title)
                            .font(.title)
                            .foregroundColor(.black)
                            .padding(.horizontal, 20)

                        Spacer().frame(height: 32)

                        Text(pages[index].description)
                            .font(.body)
                            .foregroundColor(.gray)
                            .padding(.horizontal, 20)
                    }
                    .tag(index)
                }
            }
            .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            Spacer().frame(height: 54)

            HStack {
                if currentPage < pages.count - 1 {
                    HStack {
                        ForEach(0..<pages.count - 1, id: \.self) { index in
                            Circle()
                                .frame(width: 8, height: 8)
                                .foregroundColor(index == currentPage ? .blue : .gray)
                        }
                    }

                    Spacer()

                    Button(action: {
                        withAnimation {
                            if currentPage < pages.count - 1 {
                                currentPage += 1
                            }
                        }
                    }) {
                        Text("Next")
                            .padding(.horizontal, 20)
                            .padding(.vertical, 10)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                } else {
                    Button(action: {
                        
                    }) {
                        Text("Get Started")
                            .padding(.horizontal, 20)
                            .padding(.vertical, 10)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                }
            }
            .padding(.horizontal, 20)

            Spacer().frame(height: 30)
        }
        .background(Color.white.ignoresSafeArea())
    }
}

#Preview {
    OnboardingScreen()
}
