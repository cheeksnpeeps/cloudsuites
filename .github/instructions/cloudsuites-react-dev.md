---
description: 'Property Management Frontend Copilot — React + Figma + Swagger'
applyTo: '**/*.jsx, **/*.tsx, **/*.js, **/*.ts, **/*.css, **/*.scss'
---

# CloudSuites Frontend Copilot — React + Figma + Swagger

An expert **React/TypeScript** + **Figma** copilot that builds a **professional-grade, persona-driven property-management frontend** inspired by **BuildingLink**. The copilot consumes the **CloudSuites APIs** (`http://localhost:8080/swagger-ui/index.html`, `/v3/api-docs`) and ensures the frontend is production-ready, accessible, and maintainable.

---

## 1. Project Context

- **Backend:** Spring Boot + PostgreSQL + Flyway, OAuth2/JWT auth, Maven build
- **Frontend:** React 19+, TypeScript, Vite, packaged via `frontend-maven-plugin` into Spring Boot `/static`
- **Design:** Figma (Variables → tokens, Components → UI kit)
- **Personas:** Admin, Owner, Staff (with subroles), Tenants
- **Inspiration:** BuildingLink features (amenity bookings, announcements, service requests, visitors, payments) but more modern and intuitive

---

## 2. Architecture

- Functional components with hooks (composition over inheritance)
- Organize by **feature/domain** (e.g., `/features/amenities`, `/features/bookings`)
- Clear separation:  
  - **UI components** (presentational, reusable)  
  - **Hooks** (stateful logic, API calls)  
  - **Pages** (route-level, persona-specific)  
- Shared lib for **RBAC**, **http client**, **design tokens**

---

## 3. Personas & UX

Each persona drives **navigation, features, and permissions**.

- **Admin:** multi-building dashboard, staff/role management, integrations, billing, reports, audit logs  
- **Owner:** ownership summary, unit details, payments, documents, approvals, communications  
- **Staff:**  
  - *Property Manager*: bookings approval, resident issues, announcements  
  - *Leasing Agent*: leads pipeline, applications, e-sign leases  
  - *Maintenance Tech*: work orders, parts, before/after photos  
  - *Security*: visitor logs, deliveries, incident reports  
  - *Accounting*: payments, arrears, GL export  
- **Tenant:** bookings, service requests, visitors/deliveries, payments, unit documents, messages

**Routes:**  

- `/tenant/*`, `/owner/*`, `/staff/*`, `/admin/*`  
Each with its own **shell layout**, but sharing a unified design system.

---

## 4. Figma Workflow

- **Design tokens:** export Variables (color, typography, spacing, radii, shadows) → Tailwind config + CSS vars  
- **Component library:** buttons, cards, inputs, tables, modals, navbars, toasts → implemented with shadcn/ui + Radix primitives  
- **Responsive design:** prioritize mobile-first for Tenants/field Staff; full dashboards for Admin/Owner  
- **Dark/Light modes** supported by Figma token sets

---

## 5. API Integration (Swagger-driven)

- Always derive types/hooks from `api-docs.json` using **orval** or `openapi-typescript`  
- Place generated client in `src/lib/api.ts` and wrap with a shared `http.ts` (Axios + JWT interceptor)  
- Never write ad-hoc fetch calls outside the client  
- Use **TanStack Query** for caching, retries, optimistic updates, and invalidation

**Example (manual hook):**

```ts
export const useAmenities = (buildingId: string) =>
  useQuery({
    queryKey: ['amenities', buildingId],
    queryFn: async () => (await http.get(`/v1/buildings/${buildingId}/amenities`)).data,
  });
````

---

## 6. State Management

- Local state: `useState`, `useReducer` for complex logic
- Shared UI state: Context or Zustand
- Server state: TanStack Query (cache + retries)
- RBAC: `hasPerm(role, action, resource)` helper (role- and scope-aware)

---

## 7. Styling

- Tailwind CSS with tokens synced from Figma
- Utility classes for consistency; extend theme via tokens
- Accessible states (focus rings, hover, disabled, errors)
- Responsive grid layouts; mobile-first breakpoints

---

## 8. Forms & Validation

- **React Hook Form + Zod** schemas for validation
- Standardized `FormField` wrapper: label, error, hint, input
- Async validation (e.g., tenant email uniqueness) with debounce
- Accessible inputs: `aria-describedby`, proper error messages

---

## 9. Error Handling

- Global **ErrorBoundary** for React component errors
- Axios interceptors for API errors:

  - 401 → login
  - 403 → no-access page
  - 404 → friendly empty state
  - 409 → conflict toast
  - 5xx → retry/backoff
- Query ErrorBoundary per route
- User-facing errors: inline for forms, toast for global events

---

## 10. Accessibility

- WCAG AA compliance, semantic HTML, ARIA attributes
- Dialogs with focus traps + Escape close
- Menus navigable with arrow keys
- Images/icons: alt text or `aria-hidden`
- Test with axe-core in CI and screen readers manually

---

## 11. Performance

- Route-based code splitting (`lazy` + `Suspense`)
- Virtualize lists >200 items
- Lazy-load images; skeletons instead of spinners
- Profile with React DevTools, Lighthouse CI budget (LCP <2.5s)
- Bundle size targets enforced

---

## 12. Testing

- **Unit tests:** hooks + utilities
- **Component tests:** key UI components with React Testing Library
- **Integration tests:** complex flows (e.g., Booking Wizard)
- **e2e tests:** Playwright happy paths (Amenity booking, OTP login, Work order close)
- **Accessibility tests:** axe-core in CI

---

## 13. Implementation Process

1. Generate API client from Swagger (`orval`)
2. Sync tokens from Figma → Tailwind theme
3. Scaffold persona routes + page shells
4. Build reusable components from Figma exports
5. Implement feature flows end-to-end (Amenity booking, Service requests, etc.)
6. Add RBAC guards for routes + actions
7. Harden with error handling + accessibility
8. Write tests (unit → e2e)
9. Optimize performance + bundle size
10. Ship via Maven build into Spring Boot static resources

---

## 14. Definition of Done (DoD)

* Matches Swagger contract (typesafe)
* Uses tokens + components from design system
* Accessible (axe score ≥ 90, keyboard nav works)
* Responsive and mobile-first where required
* Handles loading/error/empty states gracefully
* Tested (unit, component, e2e for happy paths)
* Documented (Storybook for components; README for features)

---

## 15. Example Execution Plan

**Feature:** Tenant → Book Amenity

- **Endpoints:**

  - `GET /v1/buildings/{buildingId}/amenities`
  - `GET /v1/amenities/{amenityId}/availability?start&end`
  - `POST /v1/amenities/{amenityId}/tenants/{tenantId}/bookings`
- **Routes:**

  - `/tenant/amenities` (list view)
  - `/tenant/amenities/:id` (details + calendar)
  - `/tenant/amenities/:id/book` (wizard)
- **Components:** `AmenityCard`, `AvailabilityCalendar`, `BookingWizard`, `BookingSummary`
- **Validation:** Zod schema (slot required, guests ≤ capacity)
- **A11y:** keyboardable calendar; descriptive errors
- **Tests:** hook unit test, wizard component test, Playwright happy-path test

---

## 16. Copilot Commands

- “Plan Amenity Booking flow for Tenant” → EXECUTION PLAN (routes, components, API hooks, tests)
- “Build Tenant → Book Amenity happy path” → generate code diffs (routes, hooks, components, tests)
- “Wire RBAC for Staff roles” → add role enums, guard helpers, protected routes
- “Sync tokens with Figma” → update Tailwind + CSS vars
- “Create API client from Swagger” → generate types/hooks + usage samples
- “Harden page X” → add skeletons, error boundaries, retries
- “Add tests for flow Y” → Vitest + Testing Library + Playwright spec