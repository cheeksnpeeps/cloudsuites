# CloudSuites Frontend Engineering Standards

This document is the **guidebook** for building the CloudSuites frontend.  
It complements the chatmode (`cloudsuites-frontend-copilot.md`).

---

## 1. Project Context

- Backend: Spring Boot + PostgreSQL, Flyway, OAuth2/JWT (Auth0/Okta)  
- Frontend: React 19+, TypeScript, Vite, Tailwind + shadcn/ui  
- Packaged with Maven (`frontend-maven-plugin`) into `/static`  
- Personas: Admin, Owner, Staff (Manager, Leasing Agent, Maintenance, Security, Accounting), Tenant  
- Design: Figma tokens + components as the source of truth  

---

## 2. Architecture

- Functional components with hooks; composition > inheritance  
- Organize by **feature/domain**  
- Separate: components (UI), hooks (logic), services (API), pages (routes)  
- Shared libraries: RBAC, http client, design tokens  

---

## 3. API Integration

- Use `orval` or `openapi-typescript` to generate types/hooks from `api-docs.json`  
- Place client in `src/lib/api.ts`, axios wrapper in `src/lib/http.ts`  
- Use TanStack Query for server state (cache, retries, optimistic updates)  
- No ad-hoc fetch outside client  

---

## 4. Personas & Routing

- **Admin**: buildings, users, integrations, billing, reports, audit  
- **Owner**: units, statements, documents, requests  
- **Staff**: role-aware dashboards (bookings, work orders, visitor logs, etc.)  
- **Tenant**: unit info, amenity bookings, requests, deliveries, payments  
- Routes: `/admin/*`, `/owner/*`, `/staff/*`, `/tenant/*`  
- Guard routes with `hasPerm(role, action, resource)`

---

## 5. Figma → Code Workflow

- Export tokens (colors, spacing, typography, radii, shadows) → `tokens.json`  
- Map to `tailwind.config.ts` + CSS vars  
- Implement core UI primitives (Button, Card, Input, Table, Tabs, Dialog, Toast, Calendar, Sidebar)  
- Apply responsive design constraints from Figma frames  
- Maintain a Storybook with token pages + component states  

---

## 6. Forms & Validation

- Use **React Hook Form + Zod** schemas  
- Standard `FormField` wrapper: label, hint, error, input  
- Async validations debounced  
- Accessible inputs: labels + `aria-describedby`  

---

## 7. Error Handling

- Global `ErrorBoundary` + per-route QueryErrorResetBoundary  
- Axios interceptors:  
  - 401 → login  
  - 403 → no-access page  
  - 404 → friendly empty  
  - 409 → toast conflict  
  - 5xx → retry/backoff  
- Inline errors for forms, toast for global events  

---

## 8. Accessibility

- WCAG AA compliance required  
- Keyboard nav, ARIA attributes, semantic HTML  
- Dialogs: focus traps + Escape close  
- Menus: arrow-key nav  
- Test with axe-core in CI  

---

## 9. Performance

- Route-based code splitting (`lazy`, `Suspense`)  
- Virtualize lists >200 items  
- Lazy-load images  
- Skeletons instead of spinners  
- Lighthouse CI budgets: LCP < 2.5s  

---

## 10. Testing

- Unit: hooks + utilities (Vitest)  
- Component: React Testing Library  
- Integration: complex flows (Booking Wizard, Work Order)  
- E2E: Playwright (Amenity booking, OTP login, Work order close)  
- Accessibility: axe-core in CI  

---

## 11. Implementation Loop

1. Generate API client  
2. Sync tokens → Tailwind → Storybook  
3. Scaffold persona routes + shells  
4. Build reusable components  
5. Implement flow end-to-end  
6. Apply RBAC guards  
7. Harden with errors + a11y  
8. Add tests (unit → e2e)  
9. Perf optimizations  
10. Ship via Maven build  

---

## 12. Example Execution Plan

**Feature:** Tenant → Book Amenity

- Endpoints:  
  - `GET /v1/buildings/{buildingId}/amenities`  
  - `GET /v1/amenities/{amenityId}/availability`  
  - `POST /v1/amenities/{amenityId}/tenants/{tenantId}/bookings`  
- Routes:  
  - `/tenant/amenities` → list  
  - `/tenant/amenities/:id` → details + calendar  
  - `/tenant/amenities/:id/book` → wizard  
- Components: `AmenityCard`, `AvailabilityCalendar`, `BookingWizard`  
- Validation: slot required, guests ≤ capacity  
- A11y: keyboard calendar; proper labels/errors  
- Tests: hook unit test, wizard component test, Playwright e2e  

---

## 13. Definition of Done

- Types match Swagger  
- Tokens + design system applied  
- Accessible (axe ≥ 90)  
- Responsive layouts  
- Error/loading/empty states covered  
- Unit + component + e2e tests passing  
- Docs/Storybook updated  

## OpenAPI Integration Standards

1. **Spec Location**  
   - Local dev: `http://localhost:8080/v3/api-docs`  
   - Production: `${API_BASE}/v3/api-docs`

2. **Client Generation**  
   - Use `orval` (preferred) or `openapi-typescript` to generate types and React Query hooks.  
   - Commit generated clients (`src/lib/api.ts`), not just the config.  
   - Add a `yarn api:generate` script to refresh clients.  

3. **Contract-First Development**  
   - New features must start by reviewing the spec and listing relevant endpoints.  
   - UIs (forms, tables, detail views) must map directly to request/response schemas.  
   - No custom DTOs unless wrapping or combining existing ones.

4. **Validation & Error Handling**  
   - Use Zod schemas derived from OpenAPI types for runtime validation of external responses.  
   - Map API error codes (400, 401, 403, 404, 409, 422, 500) to consistent UI states.

5. **Testing**  
   - Add unit tests that assert generated types cover all request/response shapes.  
   - Use Prism or MSW mocks generated from OpenAPI for Playwright e2e.  
   - CI must fail if regenerated client differs (`git diff --exit-code`).

6. **Traceability**  
   - Every PR implementing a flow must cite the OpenAPI endpoints used.  
   - Execution plans must include endpoint → UI mapping tables.

## Beast Mode

- Engineers and agents are expected to execute without hesitation on frontend tasks if they are confident in the answer.  
- Do not block progress by asking unnecessary questions — assume responsibility for implementing React, TypeScript, and Figma best practices.  
- The only time to stop and ask is when:
  - The OpenAPI contract leaves ambiguity
  - Business rules are unclear from the API or requirements
  - There is a conflict between design and API capabilities
- In all other cases: **act, deliver, and move forward.**
